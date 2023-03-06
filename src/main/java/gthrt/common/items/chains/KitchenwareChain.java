package gthrt.common.items.chains;

import gthrt.GTHRTMod;
import gthrt.common.market.MarketHandler;
import gthrt.common.market.MarketBase;
import gthrt.common.HRTConfig;
import gthrt.common.items.HRTItems;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.fluids.fluidType.FluidTypes;
import gregtech.api.unification.material.Material;
import gregtech.api.recipes.GTRecipeHandler;
import gregtechfoodoption.machines.MetaTileEntityMicrowave;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.ingredients.GTRecipeItemInput;
import gregtech.common.items.MetaItems;
import gregtech.loaders.recipe.CraftingComponent;
import static gregtech.api.recipes.RecipeMaps.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialIconSet.FINE;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.GTValues.*;

import gregtechfoodoption.machines.GTFOTileEntities;

import java.util.HashMap;
import java.util.Map;

public class KitchenwareChain implements IMarketChain{
	public static final String MARKET_KEY = "kitchenware";

	public static MetaItem<?>.MetaValueItem PAN;
	public static MetaItem<?>.MetaValueItem PAN_NONSTICK;
	public static MetaItem<?>.MetaValueItem POT;
	public static MetaItem<?>.MetaValueItem SPONGE;
	public static MetaItem<?>.MetaValueItem BLENDER;
	public static MetaItem<?>.MetaValueItem TOASTER;

	public static Map<Material,Integer> handleMaterials;
	public static Map<Material,Integer> wireMaterials;
	public static Map<Material,Integer> containerMaterials;
	public static Material[] hullMaterials;

	public static Material Formaldehyde;
	public static Material Bakelite;
	public static Material WoodFiber;
	public static Material Phosgene;
	public static Material Aniline;
	public static Material MDI;
	public static Material PolyethyleneGlycol;
	public static Material Polyurethane;


	public void registerItems(int offset){
		boolean isEnable = getEnable();
		PAN=HRTItems.addMarketItem(offset,"pan",MARKET_KEY,0.1f,isEnable);
		PAN_NONSTICK=HRTItems.addMarketItem(offset+1,"pan_nonstick",MARKET_KEY,0.3f,isEnable);
		POT=HRTItems.addMarketItem(offset+2,"pot",MARKET_KEY,0.15f,isEnable);
		SPONGE=HRTItems.addMarketItem(offset+3,"sponge",MARKET_KEY,0.05f,isEnable);
		BLENDER=HRTItems.addMarketItem(offset+4,"blender",MARKET_KEY,0.25f,isEnable);
		TOASTER=HRTItems.addMarketItem(offset+5,"toaster",MARKET_KEY,0.25f,isEnable);
		if(isEnable && GTHRTMod.hasGTFO){
			float i = 0.3f;
			for(MetaTileEntityMicrowave michaelwave : GTFOTileEntities.MICROWAVE){
				MarketHandler.makeSellable(michaelwave.getStackForm(),MARKET_KEY,i);
				i*=2;
			}
		}

	};
	public void handleMaterials(int offset){

		wireMaterials = new HashMap<Material,Integer>(){{	put(Kanthal,6);
															put(Nichrome,4);
															put(TungstenSteel,2);}};
		containerMaterials = new HashMap<Material,Integer>(){{	put(Steel,2);
																put(Aluminium,3);
																put(StainlessSteel,1);}};
		for(Material entry : wireMaterials.keySet()){
			entry.addFlags(GENERATE_FINE_WIRE);
		}

		Formaldehyde = new Material.Builder(offset, "formaldehyde")
            						.fluid().colorAverage()
            						.components(Carbon, 1, Hydrogen, 2, Oxygen, 1)
            						.build();

		Bakelite = new Material.Builder(offset+1, "bakelite")
            						.color(0x1e1812)
            						.polymer().flags(GENERATE_PLATE)
            						.components(Phenol, 1, Formaldehyde, 1)
            						.build();
        WoodFiber = new Material.Builder(offset+2, "woodfiber")
            						.color(0x879181)
            						.dust().flags(DISABLE_DECOMPOSITION)
            						.components(Carbon, 12, Hydrogen, 20, Oxygen, 10)
            						.build();
		Phosgene = new Material.Builder(offset+3, "phosgene")
            						.color(0x9cbd89)
            						.fluid(FluidTypes.GAS)
            						.components(Carbon, 1, Oxygen, 1, Chlorine, 2)
            						.build();
		Aniline = new Material.Builder(offset+4, "aniline")
            						.color(0x3b2713)
            						.fluid().flags(DISABLE_DECOMPOSITION)
            						.components(Carbon, 6, Hydrogen, 7, Nitrogen, 1)
            						.build();
		MDI = new Material.Builder(offset+6, "mdi")
            						.color(0x3b2713)
            						.fluid().flags(DISABLE_DECOMPOSITION)
            						.components(Carbon, 15, Hydrogen, 10, Nitrogen, 2, Oxygen, 2)
            						.build();
        PolyethyleneGlycol = new Material.Builder(offset+7, "polyethyleneglycol")
            						.color(0xaeae9e)
            						.iconSet(FINE).flags(DISABLE_DECOMPOSITION)
            						.dust()
            						.components(Carbon, 2, Hydrogen, 4, Oxygen, 1)
            						.build();

		if(HRTConfig.replaceConstructionFoam && getEnable()){
			ConstructionFoam = new Material.Builder(offset+8, "polyurethane")
            						.color(0xbca783)
            						.fluid()
            						.build();
			Polyurethane = ConstructionFoam;
		}
		else{
			Polyurethane = new Material.Builder(offset+8, "polyurethane")
            						.color(0xbca783)
            						.fluid()
            						.build();
		}


		handleMaterials = new HashMap<Material,Integer>(){{	put(Polyethylene,2);
															put(Bakelite,1);
															put(Epoxy,1);}};
		hullMaterials = new Material[] {Bakelite, PolyvinylChloride, Steel, Aluminium};
		for(Material entry : handleMaterials.keySet()){
			entry.addFlags(GENERATE_ROD,GENERATE_BOLT_SCREW);
		}

	};
	public void registerRecipes(){


		CHEMICAL_RECIPES.recipeBuilder() //Phosgene
			.fluidInputs(CarbonMonoxide.getFluid(1000))
			.fluidInputs(Chlorine.getFluid(2000))
			.fluidOutputs(Phosgene.getFluid(1000))
			.duration(270).EUt(VA[LV]).buildAndRegister();

		CHEMICAL_RECIPES.recipeBuilder() //Formaldehyde
			.fluidInputs(Methanol.getFluid(2000))
			.fluidInputs(Oxygen.getFluid(2000))
			.fluidOutputs(Formaldehyde.getFluid(2000))
			.fluidOutputs(Water.getFluid(2000))
			.duration(270).EUt(VA[LV]).buildAndRegister();

		CHEMICAL_RECIPES.recipeBuilder() //Aniline HV
			.fluidInputs(Nitrobenzene.getFluid(1000))
			.fluidInputs(Hydrogen.getFluid(2000))
			.fluidOutputs(Aniline.getFluid(1000))
			.fluidOutputs(Water.getFluid(2000))
			.duration(80).EUt(VA[HV]).buildAndRegister();

		CHEMICAL_RECIPES.recipeBuilder() //Aniline LV
			.fluidInputs(Ammonia.getFluid(1000))
			.fluidInputs(Phenol.getFluid(1000))
			.fluidOutputs(Aniline.getFluid(1000))
			.fluidOutputs(Water.getFluid(1000))
			.duration(600).EUt(VA[LV]).buildAndRegister();

		CHEMICAL_RECIPES.recipeBuilder() //MDI
			.fluidInputs(Aniline.getFluid(2000))
			.fluidInputs(Formaldehyde.getFluid(1000))
			.fluidInputs(Phosgene.getFluid(2000))
			.fluidOutputs(MDI.getFluid(1000))
			.fluidOutputs(HydrochloricAcid.getFluid(4000))
			.duration(160).EUt(VA[MV]).buildAndRegister();

		CHEMICAL_RECIPES.recipeBuilder() //Polyethylene Glycol
			.fluidInputs(Ethylene.getFluid(1000))
			.fluidInputs(Oxygen.getFluid(1000))
			.notConsumable(OrePrefix.dust,Silver)
			.circuitMeta(3)
			.output(OrePrefix.dust, PolyethyleneGlycol,7)
			.duration(160).EUt(VA[MV]).buildAndRegister();

		CHEMICAL_RECIPES.recipeBuilder()//Polyurethane
			.fluidInputs(MDI.getFluid(1000))
			.input(OrePrefix.dust, PolyethyleneGlycol,7)
			.fluidOutputs(Polyurethane.getFluid(12000))
			.duration(20).EUt(VA[LV]).buildAndRegister();
		if(HRTConfig.replaceConstructionFoam){//Remove normal construction foam recipe if we're replacing it
			GTRecipeHandler.removeRecipesByInputs(MIXER_RECIPES,new ItemStack[] {OreDictUnifier.get(OrePrefix.dust, RawRubber)},new FluidStack []{Concrete.getFluid(576)});
		}


		CHEMICAL_RECIPES.recipeBuilder()//Wood Fiber from wood
			.fluidInputs(Water.getFluid(1000))
			.input(OrePrefix.dust, Wood)
			.input(OrePrefix.dust, SodiumHydroxide, 3)
			.output(OrePrefix.dust, WoodFiber)
			.duration(1800).EUt(VA[LV]).buildAndRegister();

		CHEMICAL_RECIPES.recipeBuilder()//Wood Fiber from paper
			.fluidInputs(Water.getFluid(1000))
			.input(OrePrefix.dust, Paper)
			.input(OrePrefix.dust, SodiumHydroxide, 3)
			.output(OrePrefix.dust, WoodFiber)
			.duration(1200).EUt(VA[LV]).buildAndRegister();

		FLUID_SOLIDFICATION_RECIPES.recipeBuilder() //Polyurethane sponges
			.fluidInputs(Polyurethane.getFluid(100))
			.notConsumable(MetaItems.SHAPE_MOLD_INGOT)
			.output(SPONGE)
			.duration(10).EUt(VA[LV]).buildAndRegister();

		FORMING_PRESS_RECIPES.recipeBuilder() //Wood fiber sponges
			.input(OrePrefix.dust,WoodFiber)
			.notConsumable(MetaItems.SHAPE_MOLD_INGOT)
			.output(SPONGE)
			.duration(300).EUt(VA[LV]).buildAndRegister();

		CHEMICAL_RECIPES.recipeBuilder()//Bakelite
			.fluidInputs(Formaldehyde.getFluid(1000))
			.fluidInputs(Phenol.getFluid(1000))
			.fluidOutputs(Bakelite.getFluid(2592))
			.duration(800).EUt(VA[LV]).buildAndRegister();
		for(Map.Entry<Material,Integer> c : containerMaterials.entrySet()){
			for(Map.Entry<Material,Integer> h : handleMaterials.entrySet()){
				ASSEMBLER_RECIPES.recipeBuilder()//Pots
					.input(OrePrefix.plate,c.getKey(),2*c.getValue())
					.input(OrePrefix.bolt,h.getKey(),2*h.getValue())
					.output(POT)
					.duration(180).EUt(VA[ULV]).buildAndRegister();
				ASSEMBLER_RECIPES.recipeBuilder()//Pots
					.input(OrePrefix.plate,c.getKey(),c.getValue())
					.input(OrePrefix.stick,h.getKey(),h.getValue())
					.output(PAN)
					.duration(180).EUt(VA[ULV]).buildAndRegister();


			}
		}

		CHEMICAL_BATH_RECIPES.recipeBuilder()//Nonstick
				.fluidInputs(Polytetrafluoroethylene.getFluid(144))
				.input(PAN)
				.output(PAN_NONSTICK)
				.duration(200).EUt(VA[MV]).buildAndRegister();
		for(Material h : hullMaterials){
			for(Map.Entry<Material,Integer> w : wireMaterials.entrySet()){
				ASSEMBLER_RECIPES.recipeBuilder()//Toasters
					.input(OrePrefix.plate,h,4)
					.input(OrePrefix.wireFine,w.getKey(),w.getValue())
					.input(OrePrefix.cableGtSingle,Copper,2)
					.output(TOASTER)
					.duration(480).EUt(VA[LV]).buildAndRegister();
			}
			for(int i =1; i<=HV; i++){
				ASSEMBLER_RECIPES.recipeBuilder()//Blender
					.input(OrePrefix.plate,h,4*i)
					.input(GTRecipeItemInput.getOrCreate((ItemStack)CraftingComponent.MOTOR.getIngredient(i)))
					.input(OrePrefix.cableGtSingle,((UnificationEntry)CraftingComponent.CABLE.getIngredient(i)).material,i)
					.output(BLENDER,i)
					.duration(480).EUt(VA[LV]).buildAndRegister();
			}
		}



	};
	public boolean getEnable(){
		return HRTConfig.getEnable(MARKET_KEY);//boilerplate but I don't see another way
	}

	public void registerMarket(){
        MarketHandler.defineSellMarket(new MarketBase(MARKET_KEY,
                                                        46,2500,
                                                        0.3f,
                                                        0xeb8218));
	};
}
