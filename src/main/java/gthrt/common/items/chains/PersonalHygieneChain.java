package gthrt.common.items.chains;


import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;

import gregtech.api.unification.material.Material;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.common.items.MetaItems;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.recipes.ingredients.IntCircuitIngredient;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.unification.material.Material;
import gregtech.api.recipes.GTRecipeHandler;
import gregtech.api.fluids.fluidType.FluidTypes;
import static gregtech.api.recipes.RecipeMaps.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_FINE_WIRE;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_ROD;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_BOLT_SCREW;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_FOIL;
import static gregtech.api.unification.material.info.MaterialFlags.DISABLE_DECOMPOSITION;
import static gregtech.api.unification.material.info.MaterialIconSet.FLINT;
import static gregtech.api.GTValues.*;

import gregtechfoodoption.GTFOMaterialHandler;

import gthrt.common.HRTItems;
import gthrt.GTHRTMod;
import gthrt.common.HRTUtils;
import gthrt.common.items.MarketValueComponent;


import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;


public class PersonalHygieneChain{
	public static MetaItem<?>.MetaValueItem TOOTHBRUSH;
	public static final Map<Material,Integer> brissleMaterials = new HashMap<Material,Integer>()
	{{;
		put(SiliconeRubber,3);
		put(Polycaprolactam,6);
	}};
	public static final Map<Material,Integer> stickMaterials = new HashMap<Material,Integer>()
	{{;
		put(Polyethylene,2);
		put(Epoxy,4);
		put(Polybenzimidazole,6);
	}};

	public static MetaItem<?>.MetaValueItem TOOTHPASTE;
	public static final Material Fluorophosphate = new Material.Builder(24000, "sodium_fluorophosphate")
            											.dust().color(0xccc746)
            											.components(Sodium, 2,Phosphorus, 1, Fluorine, 1, Oxygen, 3)
            											.build();
    public static final Material AluminiumHydroxide = new Material.Builder(24001, "aluminium_hydroxide")
            											.dust().iconSet(FLINT).color(0xf7a7bd)
            											.components(Aluminium, 1, Oxygen, 1, Hydrogen, 1)
            											.build();
    public static final Material SodiumAluminate = new Material.Builder(24002, "sodium_aluminate")
            											.dust().color(0xf6dfe6)
            											.components( Sodium, 1,Aluminium, 1, Oxygen, 2)
            											.build();
    public static final Material ToothpastePaste = new Material.Builder(24003, "toothpaste_paste")
            											.fluid().color(0xf0e292)
            											.build();
    public static final Material DifluorophosphoricAcid = new Material.Builder(24004, "difluorophosphoric_acid")
            											.fluid().color(0xdbf50c)
            											.components(Fluorine,2,Hydrogen,1,Oxygen,2,Phosphorus,1)
            											.build();
	public static final Material AmmoniumFluoride = new Material.Builder(24005, "ammonium_fluoride")
            											.dust().color(0xadc85b).iconSet(FLINT)
            											.components(Ammonia,1,Fluorine,1)
            											.build();
	public static final Map<Material,Integer> tubeMaterials = new HashMap<Material,Integer>()
	{{;
		put(Steel,1);
		put(Aluminium,2);
		put(PolyvinylChloride,3);
		put(Lead,4);
	}};


	public static final Material[] abbrasiveMaterials = {AluminiumHydroxide,Zeolite,Calcite};

	public static MetaItem<?>.MetaValueItem DEODORANT;
	public static final Map<Material,Integer> capMaterials = new HashMap<Material,Integer>()
	{{;
		put(Polyethylene,1);
		put(PolyvinylChloride,2);
		put(Epoxy,3);
		put(Polybenzimidazole,6);
	}};
	public static final Material FloweryEssence = new Material.Builder(24006, "flowery_essence")
            											.fluid().color(0x8ab62b)
            											.build();
	public static final Material Perfume = new Material.Builder(24007, "perfume")
            											.fluid().color(0xc5f55e).components(Methanol,1,Ethanol,1,FloweryEssence, 1)
                										.flags(DISABLE_DECOMPOSITION)
            											.build();
    public static final Material Deodorant = new Material.Builder(24008, "deodorant")
            											.fluid(FluidTypes.GAS).color(0x0d468c).components(FloweryEssence,1)
                										.flags(DISABLE_DECOMPOSITION)
            											.build();

	public static MetaItem<?>.MetaValueItem SOAP;
	public static MetaItem<?>.MetaValueItem SOAP_BASE;

	public static Material LiquidSoap;





	public static void handleMaterial(){
		//toothbrush
		for(Material m : brissleMaterials.keySet()){m.addFlags(GENERATE_FINE_WIRE);}
		for(Material m : stickMaterials.keySet()){m.addFlags(GENERATE_ROD);}
		//toothpaste
		for(Material m : tubeMaterials.keySet()){m.addFlags(GENERATE_FOIL);}
		//deodorant
		for(Material m : capMaterials.keySet()){m.addFlags(GENERATE_BOLT_SCREW);}
		//soap
		if(GTHRTMod.hasGTFO){
			GTFOMaterialHandler.SodiumStearate.addFlags(DISABLE_DECOMPOSITION);
		}
		else{
			LiquidSoap = new Material.Builder(24009, "liquid_soap")
            											.fluid().color(0x8a7e55)
            											.build();
		}

	}
	public static void registerItems(){

    	TOOTHBRUSH 	= HRTItems.HRT_ITEMS.addItem(10,"toothbrush").addComponents(new MarketValueComponent("personalhygiene",0.01f));
    	TOOTHPASTE 	= HRTItems.HRT_ITEMS.addItem(11,"toothpaste").addComponents(new MarketValueComponent("personalhygiene",0.02f));
    	DEODORANT 	= HRTItems.HRT_ITEMS.addItem(12,"deodorant").addComponents(new MarketValueComponent("personalhygiene",0.02f));
    	SOAP 		= HRTItems.HRT_ITEMS.addItem(13,"soap").addComponents(new MarketValueComponent("personalhygiene",0.05f));
    	SOAP_BASE	= HRTItems.HRT_ITEMS.addItem(14,"soap_base");
	}

	public static void registerRecipes(){
		//remove and readd nylon string recipe
		GTRecipeHandler.removeRecipesByInputs(WIREMILL_RECIPES,OreDictUnifier.get(OrePrefix.ingot, Polycaprolactam));
		WIREMILL_RECIPES.recipeBuilder()
			.input(OrePrefix.ingot, Polycaprolactam)
			.output(OrePrefix.wireFine,Polycaprolactam, 8)
			.duration(400).EUt(VA[ULV]).buildAndRegister();
		WIREMILL_RECIPES.recipeBuilder()
			.input(OrePrefix.wireFine, Polycaprolactam, 4)
			.output(Items.STRING, 16)
			.duration(40).EUt(48).buildAndRegister();
		//toothbrushes
		for(Map.Entry<Material,Integer> s : stickMaterials.entrySet()){
			ASSEMBLER_RECIPES.recipeBuilder()
				.input(Items.STRING, 4)
				.input(OrePrefix.stick, s.getKey())
				.output(TOOTHBRUSH, s.getValue())
				.duration(40*s.getValue()).EUt(VA[ULV]).buildAndRegister();
			for(Map.Entry<Material,Integer> b : brissleMaterials.entrySet()){
				ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.wireFine,b.getKey(), 4)
				.input(OrePrefix.stick, s.getKey())
				.output(TOOTHBRUSH, s.getValue()*b.getValue())
				.duration(40*s.getValue()*b.getValue()).EUt(VA[ULV]).buildAndRegister();
			}
		}
		//don't forget good old sticks
		for(Map.Entry<Material,Integer> b : brissleMaterials.entrySet()){
			ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.wireFine,b.getKey(), 4)
				.input(Items.STICK, 1)
				.output(TOOTHBRUSH, b.getValue())
				.duration(40*b.getValue()).EUt(VA[ULV]).buildAndRegister();
		}
		ASSEMBLER_RECIPES.recipeBuilder()
				.input(Items.STRING, 4)
				.input(Items.STICK, 1)
				.output(TOOTHBRUSH)
				.duration(40).EUt(VA[ULV]).buildAndRegister();


		//toothpaste
		//aluminum hydroxide
		CHEMICAL_RECIPES.recipeBuilder()
			.input(OrePrefix.dust, Bauxite,5)
			.input(OrePrefix.dust, SodiumHydroxide,3)
			.output(OrePrefix.dust, SodiumAluminate, 4)
			.output(OrePrefix.dust, AluminiumHydroxide,3)
			.fluidOutputs(Oxygen.getFluid(1000))
            .duration(270).EUt(40).buildAndRegister();
        //sodium fluorophosphate
		CHEMICAL_RECIPES.recipeBuilder()
			.fluidInputs(Ammonia.getFluid(1000))
			.fluidInputs(HydrofluoricAcid.getFluid(1000))
			.output(OrePrefix.dust,AmmoniumFluoride,2)
			.fluidOutputs(Hydrogen.getFluid(2000))
			.duration(300).EUt(VA[LV]).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder()
			.input(OrePrefix.dust, AmmoniumFluoride,8)
			.input(OrePrefix.dust, PhosphorusPentoxide,7)
			.fluidOutputs(Ammonia.getFluid(2000))
			.fluidOutputs(DifluorophosphoricAcid.getFluid(2000))
			.duration(2400).EUt(VA[LV]).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder()
			.input(OrePrefix.dust, SodiumHydroxide,6)
			.fluidInputs(DifluorophosphoricAcid.getFluid(1000))
			.output(OrePrefix.dust, Fluorophosphate,7)
			.fluidOutputs(Water.getFluid(1000))
			.fluidOutputs(HydrofluoricAcid.getFluid(1000))
			.duration(800).EUt(VA[MV]).buildAndRegister();


		//toothPASTE
		for(ArrayList<Material> M : HRTUtils.getSubsets(abbrasiveMaterials)){
			RecipeBuilder<?> RB = MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dustSmall, Fluorophosphate)
				.input(OrePrefix.dustSmall, Apatite);
			for(Material m : M){
				RB.input(OrePrefix.dustSmall, m);
			}
			RB.notConsumable(new IntCircuitIngredient(M.size()+1))
				.fluidInputs(Water.getFluid((M.size()+1)*200))
				.fluidOutputs(ToothpastePaste.getFluid((M.size()+1)*250))
				.duration(1200).EUt(VA[LV]*(M.size()+1)).buildAndRegister();


		}
		//actual tubes
		for(Map.Entry<Material,Integer> e : tubeMaterials.entrySet()){
			CANNER_RECIPES.recipeBuilder()
				.input(OrePrefix.foil,e.getKey(),e.getValue())
				.fluidInputs(ToothpastePaste.getFluid(50))
				.output(TOOTHPASTE)
				.duration(200).EUt(VA[MV]).buildAndRegister();
		}
		//deodorant
		GTRecipeHandler.removeRecipesByInputs(ASSEMBLER_RECIPES,OreDictUnifier.get(OrePrefix.dust, Redstone),MetaItems.FLUID_CELL.getStackForm());
		for(Map.Entry<Material,Integer> e : capMaterials.entrySet()){
			ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.bolt,e.getKey(),2)
				.input(MetaItems.FLUID_CELL,e.getValue())
				.output(MetaItems.SPRAY_EMPTY,e.getValue())
				.duration(100).EUt(VA[ULV]).buildAndRegister();
		}
		//I hate botania now
		MIXER_RECIPES.recipeBuilder()
			.input(Item.getItemFromBlock(Blocks.YELLOW_FLOWER),1,0)
			.fluidInputs(Methanol.getFluid(1000))
			.fluidInputs(Ethanol.getFluid(1000))
			.fluidOutputs(FloweryEssence.getFluid(2000))
			.duration(3000).EUt(VA[ULV]).buildAndRegister();
		for(BlockFlower.EnumFlowerType f : BlockFlower.EnumFlowerType.values()){
			MIXER_RECIPES.recipeBuilder()
				.input(Item.getItemFromBlock(Blocks.RED_FLOWER),1,f.getMeta())
				.fluidInputs(Methanol.getFluid(1000))
				.fluidInputs(Ethanol.getFluid(1000))
				.fluidOutputs(FloweryEssence.getFluid(2000))
				.duration(3000).EUt(VA[ULV]).buildAndRegister();
		}
		for(BlockDoublePlant.EnumPlantType f : BlockDoublePlant.EnumPlantType.values()){
			MIXER_RECIPES.recipeBuilder()
				.input(Item.getItemFromBlock(Blocks.DOUBLE_PLANT),1,f.getMeta())
				.fluidInputs(Methanol.getFluid(1000))
				.fluidInputs(Ethanol.getFluid(1000))
				.fluidOutputs(FloweryEssence.getFluid(2000))
				.duration(3000).EUt(VA[ULV]).buildAndRegister();
		}

		DISTILLERY_RECIPES.recipeBuilder()
            .circuitMeta(1)
			.fluidInputs(FloweryEssence.getFluid(100))
			.fluidOutputs(Perfume.getFluid(10))
			.duration(120).EUt(VA[MV]*2).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder()
			.notConsumable(new IntCircuitIngredient(1))
			.fluidInputs(Methanol.getFluid(2000))
			.fluidInputs(Acetone.getFluid(100))
			.fluidInputs(Perfume.getFluid(1))
			.fluidOutputs(Deodorant.getFluid(2500))
			.duration(6000).EUt(VA[MV]).buildAndRegister();
		CHEMICAL_RECIPES.recipeBuilder()
			.notConsumable(new IntCircuitIngredient(0))
			.fluidInputs(Methanol.getFluid(1000))
			.fluidInputs(Acetone.getFluid(100))
			.fluidOutputs(Deodorant.getFluid(1000))
			.duration(6000).EUt(VA[MV]).buildAndRegister();
		CANNER_RECIPES.recipeBuilder()
				.input(MetaItems.SPRAY_EMPTY)
				.fluidInputs(Deodorant.getFluid(150))
				.output(DEODORANT)
				.duration(200).EUt(VA[MV]).buildAndRegister();
		//soap
		if(GTHRTMod.hasGTFO){
			FLUID_SOLIDFICATION_RECIPES.recipeBuilder()
				.fluidInputs(GTFOMaterialHandler.SodiumStearate.getFluid(144))
				.notConsumable(MetaItems.SHAPE_MOLD_INGOT)
				.output(SOAP_BASE)
				.duration(4000).EUt(VA[ULV]).buildAndRegister();
		}
		else{
			CHEMICAL_RECIPES.recipeBuilder()
				.fluidInputs(SeedOil.getFluid(2000))
				.input(OrePrefix.dustSmall,SodiumHydroxide)
				.fluidOutputs(LiquidSoap.getFluid(1296))
				.fluidOutputs(Glycerol.getFluid(1000))
				.duration(40).EUt(VA[MV]).buildAndRegister();
			CHEMICAL_RECIPES.recipeBuilder()
				.fluidInputs(FishOil.getFluid(1000))
				.input(OrePrefix.dustSmall,SodiumHydroxide)
				.fluidOutputs(LiquidSoap.getFluid(1296))
				.fluidOutputs(Glycerol.getFluid(1000))
				.duration(40).EUt(VA[MV]).buildAndRegister();
			CHEMICAL_RECIPES.recipeBuilder()
				.fluidInputs(SeedOil.getFluid(2000))
				.input(OrePrefix.dustSmall,Potash)
				.fluidOutputs(LiquidSoap.getFluid(1728))
				.fluidOutputs(Glycerol.getFluid(1000))
				.duration(40).EUt(VA[MV]).buildAndRegister();
			CHEMICAL_RECIPES.recipeBuilder()
				.fluidInputs(FishOil.getFluid(1000))
				.input(OrePrefix.dustSmall,Potash)
				.fluidOutputs(LiquidSoap.getFluid(1728))
				.fluidOutputs(Glycerol.getFluid(1000))
				.duration(40).EUt(VA[MV]).buildAndRegister();

			FLUID_SOLIDFICATION_RECIPES.recipeBuilder()
				.fluidInputs(LiquidSoap.getFluid(144))
				.notConsumable(MetaItems.SHAPE_MOLD_INGOT)
				.output(SOAP_BASE)
				.duration(4000).EUt(VA[ULV]).buildAndRegister();
		}
		FORMING_PRESS_RECIPES.recipeBuilder()
			.input(Items.PAPER)
			.input(SOAP_BASE)
			.output(SOAP)
			.duration(200).EUt(VA[LV]).buildAndRegister();

		//Package
		ASSEMBLER_RECIPES.recipeBuilder()
			.input(TOOTHBRUSH,16)
			.input(TOOTHPASTE,4)
			.input(SOAP,6)
			.input(DEODORANT,2)
			.output(HRTItems.HRT_PACKAGES.getItem("personalhygiene_package"))
			.duration(200).EUt(VA[LV]).buildAndRegister();
	}


}
