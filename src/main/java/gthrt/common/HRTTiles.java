package gthrt.common;

import gthrt.GTHRTMod;
import gthrt.common.port.WoodenDock;
import gthrt.common.port.SteelHarbor;
import gthrt.common.packingline.PackingLineController;

import net.minecraft.util.ResourceLocation;

import gregtech.api.GTValues;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.blocks.MetaBlocks;
import gregtech.api.recipes.ModHandler;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.items.MetaItems;
import gregtech.loaders.recipe.CraftingComponent;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockSteamCasing;
import static gregtech.common.metatileentities.MetaTileEntities.*;
import static gregtech.api.recipes.RecipeMaps.ASSEMBLER_RECIPES;
import static gregtech.api.GTValues.*;

//using 11111-11511 because funy
public class HRTTiles{
	public static WoodenDock WOODEN_DOCK;
	public static SteelHarbor STEEL_HARBOR;
	public static PackingLineController[] PACKING_CONTROLLERS = new PackingLineController[8];;

	public static void init(){
		GTHRTMod.logger.info("Registering HRTEs");
		WOODEN_DOCK = registerMetaTileEntity(1111, new WoodenDock(new ResourceLocation(GTHRTMod.MODID,"wooden_dock")));
		STEEL_HARBOR = registerMetaTileEntity(1112, new SteelHarbor(new ResourceLocation(GTHRTMod.MODID,"steel_harbor")));
		for(int i = 0;i<PACKING_CONTROLLERS.length;i++){
			PACKING_CONTROLLERS[i] = registerMetaTileEntity(1113+i,
															new PackingLineController(new ResourceLocation(GTHRTMod.MODID,"packing_controller."+GTValues.VN[i+1].toLowerCase()),i+1));
		}
	}
	public static void initRecipes(){
		for(int i =0;i<PACKING_CONTROLLERS.length; i++){
			ModHandler.addShapedRecipe("packing_controller_"+VN[i+1],PACKING_CONTROLLERS[i].getStackForm(),
				"XXX",
				"RAR",
				"-X-",
				Character.valueOf('A'),ASSEMBLER[i+1].getStackForm(),
				Character.valueOf('R'),CraftingComponent.ROBOT_ARM.getIngredient(i+1),
				Character.valueOf('X'),CraftingComponent.CIRCUIT.getIngredient(i+2),
				Character.valueOf('-'),CraftingComponent.CABLE.getIngredient(i+1));
		}
		ModHandler.addShapedRecipe("wooden_dock",WOODEN_DOCK.getStackForm(),
			"PPP",
			"CXW",
			"DDD",
			 Character.valueOf('D'), MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.PUMP_DECK),
			 Character.valueOf('X'), MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.ULV),
			 Character.valueOf('W'), WOODEN_DRUM.getStackForm(),
			 Character.valueOf('C'), WOODEN_CRATE.getStackForm(),
			 Character.valueOf('P'), OreDictUnifier.get(OrePrefix.pipeLargeItem, Materials.Tin));
		ASSEMBLER_RECIPES.recipeBuilder()
			.output(HRTTiles.STEEL_HARBOR)
			.input(MetaItems.ROBOT_ARM_LV,6)
			.input(MetaItems.CONVEYOR_MODULE_LV,3)
			.input(HULL[GTValues.LV])
			.inputs(MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.STEEL_SOLID,4))
			.input(OrePrefix.gear,Materials.Steel,4)
			.duration(400).EUt(VA[LV]).buildAndRegister();
	}
}
