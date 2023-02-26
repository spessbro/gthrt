package gthrt.common.port;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.api.recipes.ingredients.IntCircuitIngredient;
import gregtech.api.util.GTUtility;
import gregtech.api.recipes.ingredients.GTRecipeItemInput;
import gregtech.api.recipes.ingredients.GTRecipeFluidInput;
import gregtech.api.unification.stack.ItemAndMetadata;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.metatileentity.MTETrait;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.NonNullList;

import gthrt.common.HRTUtils;
import gthrt.common.market.MarketHandler;
import gthrt.common.market.Market;
import gthrt.GTHRTMod;
import gthrt.common.HRTConfig;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;

public class PortLogic extends MultiblockRecipeLogic {
	MetaTileEntityPortControllerAbstract portController;
	int currentIndex = 0;

	public PortLogic(MetaTileEntityPortControllerAbstract port){
		super(port);
		portController = port;
	}


	@Override
	protected Recipe findRecipe(long maxVoltage, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs){
		//declare constructor variables early
		List<GTRecipeInput> GTinputs= new ArrayList<GTRecipeInput>();

		//sort out sellables,coins and circuits
		boolean foundCircuit = false;
		List<ItemStack> credits = new ArrayList<ItemStack>();
		int budget = 0;
		List<ItemStack> exports = new ArrayList<ItemStack>();
		Market exportMarket = null;
		List<ItemStack> burnables = new ArrayList<ItemStack>();
		Fluid fuel = portController.getFuel();
		int fuelCap = 0;
		for(ItemStack i : GTUtility.itemHandlerToList(inputs)){
			if(i.isEmpty()){continue;}
			if(i.getItem() instanceof MetaItem){
				if(IntCircuitIngredient.isIntegratedCircuit(i) && !foundCircuit){
					foundCircuit = IntCircuitIngredient.getCircuitConfiguration(i) == currentIndex;
					continue;
				}
				long v = HRTUtils.getCreditValue(i);
				if(v!=0){
					credits.add(i);
					budget+=v;
					continue;
				}
			}
			Map.Entry<String,Float> v = MarketHandler.getValue(i); //only does exportables
			if(v!=null){
				if(exportMarket==null || exportMarket.getValue()<MarketHandler.markets.get(v.getKey()).getValue()){
					exportMarket = MarketHandler.markets.get(v.getKey());
				}
				exports.add(i);
				continue;
			}
			if(portController.fuelSetting){
				if(fuel==null){
					int burn = HRTUtils.actuallyGetBurnTime(i);
					if(burn>0){
						fuelCap += burn;
						burnables.add(i);
					}
				}

			}
		}
		if(portController.fuelSetting){ //go through liquid fuels
			if(fuel!=null){
				for(IFluidTank t : fluidInputs){
					FluidStack stack = t.getFluid();
					if(stack== null){
						continue;
					}
					if(stack.getFluid()==fuel){
						fuelCap+=stack.amount;
					}

				}
			}
		}
		foundCircuit = (portController.addedMarkets.size()==1 && budget>0 && exportMarket==null) ? true : foundCircuit;//if the list is 1 long then make the circuit optional
		if(foundCircuit){ //importing logic
			Market currentMarket = MarketHandler.markets.get(portController.addedMarkets.get(currentIndex));
			int maxImport = portController.getCap();
			ArrayList<ItemStack> recipeItemInputs = new ArrayList<ItemStack>(); //declare variables for the Recipe constructor
			if(portController.fuelSetting){ //use fuel //102400/100 = 1024 min 64 = 64
				Pair<Integer,List<ItemStack>> rounded;
				maxImport = Math.min(maxImport,Math.floorDiv(fuelCap,portController.getFuelEfficiency()));
				if(fuel==null){ //use solid fuel
					rounded = roundSolidFuel(burnables,maxImport,portController.getFuelEfficiency());
					recipeItemInputs.addAll(rounded.getValue());
					maxImport = rounded.getKey();
				}
				maxImport = Math.min(maxImport,currentMarket.approxBuyCount(budget,0));
				rounded = roundCredits(credits,maxImport,0,currentMarket);
				recipeItemInputs.addAll(rounded.getValue());
				maxImport = Math.min(rounded.getKey(),maxImport);
			}
			else{ //use freight
				maxImport = Math.min(maxImport, currentMarket.approxBuyCount(budget,portController.getFreight()));
				Pair<Integer,List<ItemStack>> rounded = roundCredits(credits,maxImport,portController.getFreight(),currentMarket);
				recipeItemInputs.addAll(rounded.getValue());
				maxImport = rounded.getKey();
			}
			if(maxImport<=0){;return null; }//just for safety
			//making the import recipe
			for(ItemStack i : recipeItemInputs){
				GTinputs.add(GTRecipeItemInput.getOrCreate(i));
			}
			//GTinputs.add(new IntCircuitIngredient(currentIndex).setNonConsumable());//add circuit so we can use the previous recipe (Needs revision)
			List<ItemStack> GToutputs = new ArrayList<ItemStack>();
			ItemAndMetadata importTarget = MarketHandler.buyMarkets.get(currentMarket.name);
			int totalImport = maxImport;
			while(totalImport != 0){
				GToutputs.add(importTarget.toItemStack(Math.min(totalImport,
											importTarget.item.getItemStackLimit())));
				totalImport-=Math.min(totalImport,importTarget.item.getItemStackLimit());
			}
			buyOnMarket(currentMarket,maxImport);
			return new Recipe(	GTinputs,
								GToutputs,
								NonNullList.create(),//ChanceOutputs
								fuel==null ? NonNullList.create() : Arrays.asList(GTRecipeFluidInput.getOrCreate(fuel,maxImport*portController.getFuelEfficiency())),
								NonNullList.create(),//fluid outputs maybe for a future plan of straight fluid imports but probs just gonna use item containers
								portController.getSpeed(), //boats and planes and w/e take the same amount of time no matter the cargo p-much
								(int)maxVoltage,//full throttle baybee
								true,//not sure if this makes sense, these aren't really recipes anyway
								false,//not a CT recipe (lol)
								null );//recipePropertyStorage not sure what it does, seems to work if null, not gonna complain

		}
		//Exporting Logic
		else if(exportMarket!=null){
			int maxCount = 0;
			for(int i =0;i<exports.size();i++){ //since we're editing the list in the loop
				Map.Entry<String,Float> v = MarketHandler.getValue(exports.get(i));
				if(maxCount ==portController.getCap()){//if we're full then don't bother
					exports.remove(i);
					i--;
					continue;}
				if(v.getKey() == exportMarket.name){
					if(maxCount+exports.get(i).getCount()>=portController.getCap()){
						maxCount = portController.getCap();
						continue;
					}
					maxCount+=exports.get(i).getCount();
				}
				else{ //get rid of the exports we don't care about
					exports.remove(i);
					i--;
				}
			}



			ArrayList<ItemStack> recipeItemInputs = new ArrayList<ItemStack>(); //declare variables for the Recipe constructor

			if(portController.fuelSetting){//use fuel if on
				maxCount =  Math.min(maxCount,fuelCap/portController.getFuelEfficiency());
				if(fuel==null){
					Pair<Integer,List<ItemStack>> rounded = roundSolidFuel(burnables,maxCount,portController.getFuelEfficiency());
					recipeItemInputs.addAll(rounded.getValue());
					maxCount = rounded.getKey();
				}
			}
			if(maxCount <= 0){return null;}
			float exportValue = 0f;
			int currentCount = 0;
			for(ItemStack i : exports){
				Map.Entry<String,Float> v = MarketHandler.getValue(i);
				if(v.getKey().equals(exportMarket.name)){ //only export items for the same market, makes for simpler logic
					if((v.getValue()/i.getCount())>  portController.getFreight()){ //only export items that are profitable
						if(currentCount+i.getCount() > maxCount){
							recipeItemInputs.add(HRTUtils.copyChangeSize(i,maxCount-currentCount));
							exportValue += exportMarket.approxSellValue((maxCount-currentCount)*v.getValue())-portController.getFreight()*(maxCount-currentCount);
							break;
						}
						exportValue += Math.floor(exportMarket.approxSellValue(v.getValue())-portController.getFreight()*i.getCount());
						recipeItemInputs.add(i);
						currentCount += i.getCount();
					}
				}
			}
			for(ItemStack i: recipeItemInputs){
				GTinputs.add(GTRecipeItemInput.getOrCreate(i));
			}
			sellOnMarket(exportMarket,maxCount);
			return new Recipe(	GTinputs,
								HRTUtils.creditsToCoins(exportValue),
								NonNullList.create(),//ChanceOutputs
								fuel==null ? NonNullList.create() : Arrays.asList(GTRecipeFluidInput.getOrCreate(fuel,maxCount*portController.getFuelEfficiency())),
								NonNullList.create(),//fluid outputs maybe for a future plan of straight fluid imports but probs just gonna use item containers
								portController.getSpeed(), //boats and planes and w/e take the same amount of time no matter the cargo p-much
								Math.toIntExact(maxVoltage), //I paid for all the energy hatches so I'll use all the energy hatches
								true,//not sure if this makes sense, these aren't really recipes anyway
								false,//not a CT recipe (lol)
								null);//recipePropertyStorage not sure what it does, seems to work if null, not gonna complain

		}
		return null; //if we come up short


	}


	protected Pair<Integer,List<ItemStack>> roundSolidFuel(List<ItemStack> burnables,int target,int efficiency){ // 16*6400,58,100
		List<ItemStack> out = new ArrayList<ItemStack>();
		burnables.sort(Collections.reverseOrder(new HRTUtils.SortByBurn())); //bigger fuel first

		int targetBurn = target*efficiency;//5800

		for(ItemStack b : burnables){
			int time = HRTUtils.actuallyGetBurnTime(b);
			if(time > targetBurn){//102400 > 5800
				if(b.getCount() == 1){//if it's just one big fuel then let the player waste it, They're the big silly tho
					out.add(b);
					targetBurn = 0;
					break;
				}
				if(targetBurn % (time/b.getCount()) > time/(b.getCount()*2) || targetBurn <= time/b.getCount()){ //if the next interval is closer to the target then use that. 5800%6400 = 58k > 32k
					out.add(HRTUtils.copyChangeSize(b,HRTUtils.ceilDiv(targetBurn,time/b.getCount())));
					targetBurn = 0;
					break;
				}
				if(targetBurn/(time/b.getCount())!=0){ //if we're rounding down, we have to adjust our target. 6400//6400 = 1
					out.add(HRTUtils.copyChangeSize(b,Math.floorDiv(targetBurn,time/b.getCount())));
					targetBurn%= time/b.getCount(); // 5800 % 6400 = 58k
				}
			}
			else{
				out.add(b);
				targetBurn -= time; //add the item to the list and remove the burn time
			}
		}
		if(targetBurn!=0){
			target -= HRTUtils.ceilDiv(targetBurn,efficiency);
		}
		return new ImmutablePair<Integer,List<ItemStack>>(target,out);
	}


	protected Pair<Integer,List<ItemStack>> roundCredits(List<ItemStack> credits, int target, float freight, Market market){ //this is essentially the same method except with coins and freight
		List<ItemStack> out = new ArrayList<ItemStack>(); //6*8; 3; 13.1
		credits.sort(Collections.reverseOrder(new HRTUtils.SortCreditStacks()));

		float targetFreight = target*freight + market.approxBuyValue(target);
		int totalValue = 0;

		for(ItemStack c : credits){
			int value = HRTUtils.getCreditValue(c);//48

			if(value > targetFreight){
				if(c.getCount() == 1){
					out.add(c);
					return new ImmutablePair<Integer,List<ItemStack>>(target,out);
				}
				if(targetFreight % (value/c.getCount()) > value/(c.getCount()*2) || targetFreight <= value/c.getCount()){ // 39 % 8 = 7 > 4
					out.add(HRTUtils.copyChangeSize(c,HRTUtils.ceilDiv(targetFreight,value/c.getCount())));//39 // 8 = 5
					return new ImmutablePair<Integer,List<ItemStack>>(target,out);
				}
				if(Math.floorDiv((int)targetFreight,value/c.getCount())!=0){
					out.add(HRTUtils.copyChangeSize(c,Math.floorDiv((int)targetFreight,value/c.getCount())));
					totalValue += Math.floorDiv((int)targetFreight,value/c.getCount())*value/c.getCount();
				}
			}
			else{
				out.add(c);
				totalValue += value;
			}
		}
		target = market.approxBuyCount(totalValue,freight);


		return new ImmutablePair<Integer,List<ItemStack>>(target,out);
	}

	@Override
	protected void completeRecipe(){
		previousRecipe=null;
		super.completeRecipe();
	}
	@Override
	protected boolean checkPreviousRecipe(){
		return false;
	}
	@Override
	protected void trySearchNewRecipeDistinct() {
		for(int i=0;i<portController.addedMarkets.size();i++){
			currentIndex++;
			if(currentIndex==portController.addedMarkets.size()){currentIndex=0;}
			super.trySearchNewRecipeDistinct();//go through all the hatches.
			invalidatedInputList.clear();
			if(progressTime==1){ //if we find a recipe
				return;
			}
		}
		if(portController.addedMarkets.size()==0){ //in case the port is used for exports only; TODO:separate these two methods to go quicker
			super.trySearchNewRecipeDistinct();
			if(progressTime==1){ //if we find a recipe
				return;
			}
		}
		invalidatedInputList.addAll(getInputBuses());//if it's all no recipe then all the buses are wrong
	}

	void sellOnMarket(Market m, int volume){
		int perStep = volume/portController.getSpeed() * HRTConfig.ticksPerStep;
		m.supplyModifiers.add(new int[] {perStep,1});
		currentIndex++;
		if(currentIndex==portController.addedMarkets.size()){currentIndex=0;}

	}
	void buyOnMarket(Market m,int volume){

		int perStep = volume/portController.getSpeed() * HRTConfig.ticksPerStep;
		m.demandModifiers.add(new int[] {perStep,1});
		currentIndex++;
		if(currentIndex==portController.addedMarkets.size()){currentIndex=0;}
	}


}

