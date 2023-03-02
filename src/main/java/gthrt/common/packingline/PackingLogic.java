package gthrt.common.packingline;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.recipes.Recipe;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.unification.stack.ItemAndMetadata;
import gregtech.api.recipes.ingredients.GTRecipeItemInput;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.GTUtility;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import static gregtech.api.GTValues.*;

import gthrt.common.HRTUtils;
import gthrt.common.items.HRTItems;
import gthrt.common.market.MarketHandler;

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
		String target = null;
		Map<ItemAndMetadata,Integer> seenItems = new HashMap<ItemAndMetadata,Integer>();

		List<GTRecipeInput> recipeInputs= new ArrayList<GTRecipeInput>();

		List<ItemStack> inputsList = GTUtility.itemHandlerToList(inputs);
		for(ItemStack input : inputsList){
			Pair<String,Float> value = MarketHandler.getValue(input);
			if(value == null){return null;};
			if(target == null){
				target = value.getKey();
			}
			else if(target!=value.getKey()){
				return null;
			}
			ItemAndMetadata meta = new ItemAndMetadata(input);
			int count = seenItems.putIfAbsent(meta,0);
			count++;

			packingController.packageProgress += 1f/count * value.getValue();
			recipeInputs.add(GTRecipeItemInput.getOrCreate(HRTUtils.copyChangeSize(input,1)));

		}
		packingController.packageProgress*= Math.sqrt(inputsList.size()+packingController.tier);
		int packageCount = (int)Math.floor(packingController.packageProgress);
		packingController.packageProgress %= 1;
		return new Recipe(	recipeInputs, //inputs
							Arrays.asList(HRTItems.HRT_PACKAGES.packageFromMarketName(target,packageCount)), //outputs
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
	protected boolean setupAndConsumeRecipeInputs(@Nonnull Recipe recipe, @Nonnull IItemHandlerModifiable importInventory) {
    	if (!hasEnoughPower(calculateOverclock(recipe))){return false;}
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
