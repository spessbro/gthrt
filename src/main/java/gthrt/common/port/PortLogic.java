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

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Arrays;

public class PortLogic extends MultiblockRecipeLogic {

	int currentIndex = -1;
	List<String> marketQueue = new ArrayList<String>();

	public PortLogic(MetaTileEntityPortControllerAbstract port){
		super(port);
		marketQueue.add("rubber");
		currentIndex = 0;
	}

	public void toggleQueue(){
		if(marketQueue.size()>1){
			currentIndex = 0;
		}
	}


	@Override
	protected Recipe findRecipe(long maxVoltage, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs) {
		MetaTileEntityPortControllerAbstract portController = (MetaTileEntityPortControllerAbstract) metaTileEntity;
		GTHRTMod.logger.info("Looking for recipe with inputs {} and fuelsetting={}",GTUtility.itemHandlerToList(inputs),portController.fuelSetting);
		//declare constructor variables early
		List<GTRecipeInput> GTinputs= new ArrayList<GTRecipeInput>();

		//sort out sellables,coins and circuits
		boolean foundCircuit = false;
		List<ItemStack> credits = new ArrayList<ItemStack>();
		int budget = 0;
		List<ItemStack> exports = new ArrayList<ItemStack>();
		Market exportMarket = null;
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
			if(v!=null){//probs should rewrite this but I'm sick of noncompiling purgatory
				GTHRTMod.logger.info("Found sellable? {} - {}",i, v);
				if(exportMarket==null || exportMarket.currentValue<MarketHandler.markets.get(v.getKey()).currentValue){
					exportMarket = MarketHandler.markets.get(v.getKey());
				}
				exports.add(i);


				}
		}

		List<ItemStack> burnables = new ArrayList<ItemStack>();
		Fluid fuel = portController.getFuel();
		int fuelCap = 0;
		if(fuel==null){//this means we're using solid fuel
			for( ItemStack i : HRTUtils.itemHandlersToList(getInputBuses())){//can't use the first check cause you're supposed to be able to nab coal from anywhere
				int burn = HRTUtils.actuallyGetBurnTime(i);
				GTHRTMod.logger.info("Found burnable? {} - {}",i,burn);
				if(burn>0){
					fuelCap += burn;
					burnables.add(i);
				}
			}

		}
		else{
			for(IFluidTank t : fluidInputs){if(fuel == t.getFluid().getFluid()){fuelCap+=t.getFluid().amount;}}
		}
		if(foundCircuit){ //importing logic
			Market currentMarket = MarketHandler.markets.get(marketQueue.get(currentIndex));
			int maxImport = portController.getCap();
			ArrayList<ItemStack> recipeItemInputs = new ArrayList<ItemStack>(); //declare variables for the Recipe constructor
			if(portController.fuelSetting){
				maxImport = Math.min(Math.min(fuelCap/portController.getFuelEfficiency(),Math.round(budget/currentMarket.currentValue-0.5f)),maxImport);

				Pair<Integer,List<ItemStack>> rounded = roundCredits(credits,maxImport,currentMarket.currentValue);
				recipeItemInputs.addAll(rounded.getValue());
				maxImport = rounded.getKey();

				if(fuel==null){ //diverge again just had to check how much fuel was needed first
					rounded = roundSolidFuel(burnables,maxImport,portController.getFuelEfficiency());
					recipeItemInputs.addAll(rounded.getValue());
					maxImport = rounded.getKey();
				}
			}
			else{ //use freight
				maxImport = Math.min(maxImport,Math.round(budget/(portController.getFreight()+currentMarket.currentValue)-0.5f));
				Pair<Integer,List<ItemStack>> rounded = roundCredits(credits,maxImport,portController.getFreight()+currentMarket.currentValue);
				recipeItemInputs.addAll(rounded.getValue());
				maxImport = rounded.getKey();
			}
			if(maxImport<=0){return null;}//just for safety
			//making the import recipe
			for(ItemStack i : recipeItemInputs){
				GTinputs.add(GTRecipeItemInput.getOrCreate(i));
			}
			GTinputs.add(new IntCircuitIngredient(currentIndex).setNonConsumable());//add circuit so we can use the previous recipe (Needs revision)
			List<ItemStack> GToutputs = new ArrayList<ItemStack>();
			ItemAndMetadata importTarget = MarketHandler.buyMarkets.get(marketQueue.get(currentIndex));
			GTHRTMod.logger.info("Importing {} from {}",maxImport,importTarget);
			while(maxImport != 0){
				GToutputs.add(importTarget.toItemStack(Math.min(maxImport,
											importTarget.item.getItemStackLimit())));
				maxImport-=Math.min(maxImport,importTarget.item.getItemStackLimit());
			}
			return new Recipe(	GTinputs,
								GToutputs,
								NonNullList.create(),//ChanceOutputs
								fuel==null ? NonNullList.create() : Arrays.asList(GTRecipeFluidInput.getOrCreate(fuel,maxImport*portController.getFuelEfficiency())),
								NonNullList.create(),//fluid outputs maybe for a future plan of straight fluid imports but probs just gonna use item containers
								portController.getSpeed(), //boats and planes and w/e take the same amount of time no matter the cargo p-much
								Math.toIntExact(maxVoltage),//full throttle baybee
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
			else{//apply a tax if not
				maxCount = Math.min(maxCount,budget/portController.getFreight());
				Pair<Integer,List<ItemStack>> rounded = roundCredits(credits,maxCount,portController.getFreight());
				recipeItemInputs.addAll(rounded.getValue());
				maxCount = rounded.getKey();
			}
			GTHRTMod.logger.info("Getting export count of {}",maxCount);
			if(maxCount <= 0){return null;}
			float exportValue = 0f;
			int currentCount = 0;
			for(ItemStack i : exports){
				Map.Entry<String,Float> v = MarketHandler.getValue(i);
				if(v.getKey() == exportMarket.name){ //only export items for the same market, makes for simpler logic
					if(currentCount+i.getCount() > maxCount){
						recipeItemInputs.add(HRTUtils.copyChangeSize(i,maxCount-currentCount));
						break;
					}
					exportValue += Math.floor(exportMarket.currentValue*v.getValue());
					recipeItemInputs.add(i);
					currentCount += i.getCount();
				}
			}
			for(ItemStack i: recipeItemInputs){
				GTinputs.add(GTRecipeItemInput.getOrCreate(i));
			}
			GTHRTMod.logger.info("Submitting export recipe worth {}",exportValue);
			GTHRTMod.logger.info("With inputs {}",recipeItemInputs);
			GTHRTMod.logger.info("And outputs {}",HRTUtils.creditsToCoins(exportValue));
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
		GTHRTMod.logger.info("Couldn't find a recipe");
		return null; //if we come up short


	}


	protected Pair<Integer,List<ItemStack>> roundSolidFuel(List<ItemStack> burnables,int target,int efficiency){
		List<ItemStack> out = new ArrayList<ItemStack>();
		burnables.sort(new HRTUtils.SortByBurn()); //bigger fuel first
		int targetBurn = target*efficiency;
		for(ItemStack b : burnables){
			int time = HRTUtils.actuallyGetBurnTime(b);
			if(time > targetBurn){
				if(b.getCount() == 1){//if it's just one big fuel then let the player waste it, They're the big silly tho
					out.add(b);
					break;
				}
				if(targetBurn % time/b.getCount() > time/(b.getCount()*2)){ //if the next interval is closer to the target then use that.
					out.add(HRTUtils.copyChangeSize(b,HRTUtils.ceilDiv(targetBurn,time/b.getCount())));
					break;
				}
				else{ //if we're rounding down, we have to adjust our target.
					out.add(HRTUtils.copyChangeSize(b,Math.floorDiv(targetBurn,time/b.getCount())));
					target-= targetBurn % time/b.getCount() / efficiency;
				}
			}
			else{
				out.add(b);
				targetBurn -= time; //add the item to the list and remove the burn time
			}
		}
		return new ImmutablePair<Integer,List<ItemStack>>(target,out);
	}

	protected Pair<Integer,List<ItemStack>> roundCredits(List<ItemStack> credits, int target, float freight){ //this is essentially the same method except with coins and freight
		List<ItemStack> out = new ArrayList<ItemStack>();
		credits.sort(new HRTUtils.SortCreditStacks());
		int targetFreight = Math.round(target*freight);
		for(ItemStack c : credits){
			int value = HRTUtils.getCreditValue(c);
			if(value > targetFreight){
				if(c.getCount() == 1){
					out.add(c);
					break;
				}
				if(targetFreight % value/c.getCount() > value/(c.getCount()*2)){
					out.add(HRTUtils.copyChangeSize(c,HRTUtils.ceilDiv(targetFreight,value/c.getCount())));
				}
				else{
					out.add(HRTUtils.copyChangeSize(c,Math.floorDiv(targetFreight,value/c.getCount())));
					target-= targetFreight % value/c.getCount() / freight;
				}
				break;
			}
			else{
				out.add(c);
				targetFreight -= value;
			}
		}
		return new ImmutablePair<Integer,List<ItemStack>>(target,out);
	}

	@Override
	protected void completeRecipe(){
		previousRecipe=null;
		super.completeRecipe();
	}


}

