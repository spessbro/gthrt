package gthrt.common.packingline;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.recipes.Recipe;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.unification.stack.ItemAndMetadata;
import gregtech.api.recipes.ingredients.GTRecipeItemInput;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.GTUtility;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import static gregtech.api.GTValues.*;

import gthrt.common.HRTUtils;
import gthrt.GTHRTMod;
import gthrt.common.items.HRTItems;
import gthrt.common.market.MarketHandler;
import gthrt.common.items.PackageItem;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.lang3.tuple.Pair;


public class PackingLogic extends MultiblockRecipeLogic {
	PackingLineController packingController;
	public PackingLogic(PackingLineController controller){
		super(controller);
		packingController = controller;
	}

	@Override
	protected Recipe findRecipe(long maxVoltage, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs) {
		Map<ItemAndMetadata,Integer> seenItems = new HashMap<ItemAndMetadata,Integer>();
		String targetMarket = null;
		float packageProgress = 0;

		List<GTRecipeInput> recipeInputs= new ArrayList<GTRecipeInput>();

		List<ItemStack> inputsList = GTUtility.itemHandlerToList(inputs);
		for(ItemStack input : inputsList){
			Pair<String,Float> value = MarketHandler.getValue(input);
			if(value == null || input.getItem() instanceof PackageItem){return null;};
			if(targetMarket == null){
				targetMarket = value.getKey();
			}
			else if(!targetMarket.equals(value.getKey())){
				return null;
			}
			ItemAndMetadata meta = new ItemAndMetadata(input);
			if(seenItems.containsKey(meta)){
				seenItems.put(meta,seenItems.get(meta)+1);
			}
			else{
				seenItems.put(meta,1);
			}

			packageProgress += value.getValue()/Math.pow(seenItems.get(meta),1+value.getValue())/input.getCount();
			recipeInputs.add(GTRecipeItemInput.getOrCreate(HRTUtils.copyChangeSize(input,1)));

		}
		packageProgress *= (float)Math.sqrt(inputsList.size()+packingController.tier)*2;
		int packageCount = (int)Math.floor(packageProgress);
		if(packageCount < 1){return null;}
		GTHRTMod.logger.info("Making packaging recipe with inputs {} and outputs of {} {} packages with progress",inputsList,targetMarket,packageCount);
		return new Recipe(	recipeInputs, //inputs
							Arrays.asList(HRTItems.HRT_PACKAGES.packageFromMarketName(targetMarket,packageCount)), //outputs
							NonNullList.create(), //ChanceOutputs
							NonNullList.create(), //FluidInputs
							NonNullList.create(), //FluidOutputs
							400, //length
							VA[LV], //voltage
							true,
							false,
							null);//recipePropertyStorage no clue what this does

	}
	@Override
	protected boolean checkPreviousRecipe() {
        if(previousRecipe == null){return false;}
        if(previousRecipe.getEUt() > getMaxVoltage()){return false;}
        List<ItemStack> inputs = GTUtility.itemHandlerToList(packingController.getImportItems());
        for(int i =0;i<inputs.size();i++){
        	if(!previousRecipe.getInputs().get(i).acceptsStack(inputs.get(i))){
				return false;
        	}
        }
        return true;
    }

	@Override
	protected boolean setupAndConsumeRecipeInputs(@Nonnull Recipe recipe, @Nonnull IItemHandlerModifiable importInventory) {
		int[] ocResults = calculateOverclock(recipe);
		ReflectionHelper.setPrivateValue(AbstractRecipeLogic.class,this,ocResults,"overclockResults");
    	if (!hasEnoughPower(ocResults)){return false;}
		if (!packingController.canVoidRecipeItemOutputs() && !GTTransferUtils.addItemsToItemHandler(getOutputInventory(), true, recipe.getAllItemOutputs())) {
      		isOutputsFull = true;
      		return false;
    	}
    	isOutputsFull = false;
		for(ItemStack input : GTUtility.itemHandlerToList(importInventory)){
			input.shrink(1);
		}
		packingController.addNotifiedInput(importInventory);
		GTTransferUtils.addItemsToItemHandler(getOutputInventory(), false, recipe.getAllItemOutputs());
		return true;
	}



}
