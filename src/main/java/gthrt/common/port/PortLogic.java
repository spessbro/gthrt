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

import gthrt.common.HRTUtils;
import gthrt.common.market.MarketHandler;
import gthrt.common.market.Market;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Arrays;

public class PortLogic extends MultiblockRecipeLogic {

	int currentIndex = -1;
	List<String> marketQueue = new ArrayList<String>();

	public PortLogic(MetaTileEntityPortControllerAbstract port){
		init(port);
	}
	protected PortLogic init(MetaTileEntityPortControllerAbstract port){
		return (PortLogic)new MTETrait(port);
	}


	public void toggleQueue(){
		if(marketQueue.size()>1){
			currentIndex = 0;
		}
	}


	@Override
	protected Recipe findRecipe(long maxVoltage, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs) {
		MetaTileEntityPortControllerAbstract portController = (MetaTileEntityPortControllerAbstract) metaTileEntity;
		//declare constructor variables early
		List<GTRecipeInput> GTinputs= new ArrayList<GTRecipeInput>();

		//sort out sellables,coins and circuits
		boolean foundCircuit = false;
		List<ItemStack> credits;
		int budget = 0;
		List<ItemStack> exports;
		Market exportMarket;
		for(ItemStack i : GTUtility.itemHandlerToList(inputs)){
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
			if(v!= null){//probs should rewrite this but I'm sick of noncompiling purgatory
				if(exportMarket==null || exportMarket.currentValue<MarketHandler.markets.get(v.getKey()).currentValue){
					exportMarket= MarketHandler.markets.get(v.getKey());
					exports.add(i);
				}
				else if(exportMarket.name==v.getKey()){//only need to gather items we could export
					exports.add(i);
				}

				}
		}

		List<ItemStack> burnables = new ArrayList<ItemStack>();
		Fluid fuel = portController.getFuel();
		int fuelCap = 0;
		if(fuel==null){//this means we're using solid fuel
			for( ItemStack i : GTUtility.itemHandlerToList(portController.getImportItems())){//can't use the first check cause you're supposed to be able to nab coal from anywhere
				int burn = i.getItem().getItemBurnTime(i);
				if(burn>0){
					fuelCap += burn;
					burnables.add(i);
				}
			}

		}
		else{
			for(IFluidTank t : fluidInputs){if(fuel == t.getFluid().getFluid()){fuelCap+=t.getFluid().amount;}}
		}
		if(fuelCap < portController.getFuelEfficiency()){return null;}//no fuel exit early
		if(foundCircuit){ //importing logic
			Market currentMarket = MarketHandler.markets.get(marketQueue.get(currentIndex));
			int maxImport = 0;
			ArrayList<ItemStack> recipeItemInputs = new ArrayList<ItemStack>(); //declare variables for the Recipe constructor
			if(portController.fuelSetting){
				recipeItemInputs.addAll(roundCredits(credits,maxImport,currentMarket.currentValue));

				if(fuel==null){ //diverge again just had to check how much fuel was needed first
					recipeItemInputs.addAll(roundSolidFuel(burnables,maxImport,portController.getFuelEfficiency()));
				}
			}
			else{ //use freight
				recipeItemInputs.addAll(roundCredits(credits,maxImport,portController.getFreight()+MarketHandler.markets.get(currentMarket).currentValue));
			}
			if(maxImport<=0){return null;}//just for safety
			//making the import recipe
			for(ItemStack i : recipeItemInputs){
				GTinputs.add(GTRecipeItemInput.getOrCreate(i));
			}
			List<ItemStack> GToutputs = new ArrayList<ItemStack>();
			ItemAndMetadata importTarget = MarketHandler.buyMarkets.get(marketQueue.get(currentIndex));
			while(maxImport != 0){
				GToutputs.add(importTarget.toItemStack(Math.min(maxImport,
											importTarget.item.getItemStackLimit())));
				maxImport-=Math.min(maxImport,importTarget.item.getItemStackLimit());
			}
			return new Recipe(	GTinputs,
								GToutputs,
								null,//ChanceOutputs
								fuel==null ? null : Arrays.asList(GTRecipeFluidInput.getOrCreate(fuel,maxImport*portController.getFuelEfficiency())),
								null,//fluid outputs maybe for a future plan of straight fluid imports but probs just gonna use item containers
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

			maxCount =  Math.min(maxCount,Math.min(portController.getCap(),fuelCap/portController.getFuelEfficiency()));


			ArrayList<ItemStack> recipeItemInputs = new ArrayList<ItemStack>(); //declare variables for the Recipe constructor

			if(portController.fuelSetting){//use fuel if on
				if(fuel==null){
					recipeItemInputs.addAll(roundSolidFuel(burnables,maxCount,portController.getFuelEfficiency()));
				}
			}
			else{//apply a tax if not
				recipeItemInputs.addAll(roundCredits(credits,maxCount,portController.getFreight()));
			}
			float exportValue = 0f;
			int currentCount = 0;
			for(ItemStack i : exports){
				Map.Entry<String,Float> v = MarketHandler.getValue(i);
				if(v.getKey() == exportMarket.name){ //only export items for the same market, makes for simpler logic
					if(currentCount+i.getCount() > portController.getCap()){
						GTinputs.add(GTRecipeItemInput.getOrCreate(i,portController.getCap()-currentCount));
					}
					exportValue += exportMarket.currentValue*v.getValue();
					GTinputs.add(GTRecipeItemInput.getOrCreate(i));
				}
			}
			for(ItemStack i: recipeItemInputs){
				GTinputs.add(GTRecipeItemInput.getOrCreate(i));
			}

			return new Recipe(	GTinputs,
								HRTUtils.creditsToCoins(exportValue),
								null,//ChanceOutputs
								fuel==null ? null : Arrays.asList(GTRecipeFluidInput.getOrCreate(fuel,maxCount*portController.getFuelEfficiency())),
								null,//fluid outputs maybe for a future plan of straight fluid imports but probs just gonna use item containers
								portController.getSpeed(), //boats and planes and w/e take the same amount of time no matter the cargo p-much
								Math.toIntExact(maxVoltage), //I paid for all the energy hatches so I'll use all the energy hatches
								true,//not sure if this makes sense, these aren't really recipes anyway
								false,//not a CT recipe (lol)
								null );//recipePropertyStorage not sure what it does, seems to work if null, not gonna complain

		}



	}


	protected List<ItemStack> roundSolidFuel(List<ItemStack> burnables,int target,int efficiency){
		List<ItemStack> out = new ArrayList<ItemStack>();
		burnables.sort(new HRTUtils.SortByBurn()); //bigger fuel first
		int targetBurn = target*efficiency;
		for(ItemStack b : burnables){
			int time = TileEntityFurnace.getItemBurnTime(b);
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
		return out;
	}

	protected List<ItemStack> roundCredits(List<ItemStack> credits, int target, float freight){ //this is essentially the same method except with coins and freight
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
					break;
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
		return out;
	}


}

