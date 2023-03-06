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
import gregtech.api.recipes.GTRecipeHandler;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
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

import gthrt.common.items.HRTItems;
import gthrt.GTHRTMod;
import gthrt.common.HRTUtils;
import gthrt.common.market.MarketHandler;
import gthrt.common.market.MarketBase;
import gthrt.common.items.MarketValueComponent;
import gthrt.common.HRTConfig;


import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class PersonalHygieneChain implements IMarketChain{
	public static final String MARKET_KEY = "personalhygiene";
	public static MetaItem<?>.MetaValueItem TOOTHBRUSH;
	public static Map<Material,Integer> brissleMaterials;
	public static Map<Material,Integer> stickMaterials;
	public static MetaItem<?>.MetaValueItem TOOTHPASTE;
	public static Material Fluorophosphate;
    public static Material AluminiumHydroxide;
    public static Material SodiumAluminate;
	public static Material AmmoniumFluoride;
    public static Material DifluorophosphoricAcid;
    public static Material ToothpastePaste;
	public static Map<Material,Integer> tubeMaterials;
	public static Material[] abbrasiveMaterials;
	public static MetaItem<?>.MetaValueItem DEODORANT;
	public static Map<Material,Integer> capMaterials;
	public static Material FloweryEssence;
	public static Material Perfume;
    public static Material Deodorant;
	public static MetaItem<?>.MetaValueItem SOAP;
	public static MetaItem<?>.MetaValueItem SOAP_BASE;
	public static Material LiquidSoap;

	public boolean getEnable(){
		return HRTConfig.getEnable(MARKET_KEY);//boilerplate but I don't see another way
	}

	public void registerMarket(){
    		MarketHandler.defineSellMarket(new MarketBase(MARKET_KEY,
                                                          54,2000,
                                                          0.1f,
                                                          0x34b586));
    }
	private void setMaterials(){
		tubeMaterials = new HashMap<Material,Integer>()
			{{;
				put(Steel,1);
				put(Aluminium,2);
				put(PolyvinylChloride,3);
				put(Lead,4);
			}};
		capMaterials = new HashMap<Material,Integer>()
			{{;
				put(Polyethylene,1);
				put(PolyvinylChloride,2);
				put(Epoxy,3);
				put(Polybenzimidazole,6);
			}};
		stickMaterials = new HashMap<Material,Integer>()
			{{;
				put(Polyethylene,2);
				put(Epoxy,4);
				put(Polybenzimidazole,6);
			}};
		brissleMaterials = new HashMap<Material,Integer>()
			{{;
				put(SiliconeRubber,3);
				put(Polycaprolactam,6);
			}};
	}

	public void handleMaterials(int offset){

		setMaterials();
		//toothbrush
		for(Material m : brissleMaterials.keySet()){
			m.addFlags(GENERATE_FINE_WIRE);}
		for(Material m : stickMaterials.keySet()){
			m.addFlags(GENERATE_ROD);}
		//toothpaste
		for(Material m : tubeMaterials.keySet()){m.addFlags(GENERATE_FOIL);}
		Fluorophosphate = new Material.Builder(offset, "sodium_fluorophosphate")
            						.dust().color(0xccc746)
            						.components(Sodium, 2,Phosphorus, 1, Fluorine, 1, Oxygen, 3)
            						.build();
		AluminiumHydroxide = new Material.Builder(offset+1, "aluminium_hydroxide")
            						.dust().iconSet(FLINT).color(0xf7a7bd)
            						.components(Aluminium, 1, Oxygen, 1, Hydrogen, 1)
            						.build();
		SodiumAluminate = new Material.Builder(offset+2, "sodium_aluminate")
            						.dust().color(0xf6dfe6)
            						.components( Sodium, 1,Aluminium, 1, Oxygen, 2)
            						.build();
        DifluorophosphoricAcid = new Material.Builder(offset+3, "difluorophosphoric_acid")
            						.fluid().color(0xdbf50c)
            						.components(Fluorine,2,Hydrogen,1,Oxygen,2,Phosphorus,1)
            						.build();
		AmmoniumFluoride = new Material.Builder(offset+4, "ammonium_fluoride")
            						.dust().color(0xadc85b).iconSet(FLINT)
            						.components(Ammonia,1,Fluorine,1)
            						.build();
		ToothpastePaste = new Material.Builder(offset+5, "toothpaste_paste")
            						.fluid().color(0xf0e292)
            						.build();

		//deodorant
		for(Material m : capMaterials.keySet()){m.addFlags(GENERATE_BOLT_SCREW);}
		FloweryEssence = new Material.Builder(offset+6, "flowery_essence")
            			.fluid().color(0x8ab62b)
            			.build();
		Perfume = new Material.Builder(offset+7, "perfume")
            			.fluid().color(0xc5f55e).components(Methanol,1,Ethanol,1,FloweryEssence, 1)
                		.flags(DISABLE_DECOMPOSITION)
            			.build();
		Deodorant = new Material.Builder(offset+8, "deodorant")
            			.fluid(FluidTypes.GAS).color(0x0d468c).components(FloweryEssence,1)
                		.flags(DISABLE_DECOMPOSITION)
            			.build();

		//soap
		if(GTHRTMod.hasGTFO){
			GTFOMaterialHandler.SodiumStearate.addFlags(DISABLE_DECOMPOSITION);
		}
		else{
			LiquidSoap = new Material.Builder(offset+9, "liquid_soap")
            											.fluid().color(0x8a7e55)
            											.build();
		}
		abbrasiveMaterials = new Material[] {AluminiumHydroxide,Zeolite,Calcite};
	}
	public void registerItems(int offset){
		boolean isEnable = getEnable();

    	TOOTHBRUSH 	= HRTItems.addMarketItem(offset,"toothbrush",MARKET_KEY,0.07f,isEnable);
    	TOOTHPASTE 	= HRTItems.addMarketItem(offset+1,"toothpaste",MARKET_KEY,0.12f,isEnable);
    	DEODORANT 	= HRTItems.addMarketItem(offset+2,"deodorant",MARKET_KEY,0.13f,isEnable);
    	SOAP 		= HRTItems.addMarketItem(offset+3,"soap",MARKET_KEY,0.15f,isEnable);
    	SOAP_BASE	= HRTItems.HRT_ITEMS.addItem(offset+4,"soap_base").setInvisible(isEnable);

		if(isEnable){
    		OreDictUnifier.registerOre(new ItemStack(Blocks.YELLOW_FLOWER,1,0),"oreFlower");
			for(BlockFlower.EnumFlowerType f : BlockFlower.EnumFlowerType.values()){
    			OreDictUnifier.registerOre(new ItemStack(Blocks.RED_FLOWER,1,f.getMeta()),"oreFlower");
			}
			OreDictUnifier.registerOre(new ItemStack(Blocks.DOUBLE_PLANT,1,0),"oreFlower");//sunflower
			OreDictUnifier.registerOre(new ItemStack(Blocks.DOUBLE_PLANT,1,1),"oreFlower");//syringa
			OreDictUnifier.registerOre(new ItemStack(Blocks.DOUBLE_PLANT,1,4),"oreFlower");//rose
			OreDictUnifier.registerOre(new ItemStack(Blocks.DOUBLE_PLANT,1,5),"oreFlower");//paeonia
		}
	}

	public void registerRecipes(){
		//remove and readd nylon string recipe
		GTRecipeHandler.removeRecipesByInputs(WIREMILL_RECIPES,OreDictUnifier.get(OrePrefix.ingot, Polycaprolactam));
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
			.input("oreFlower")
			.fluidInputs(Methanol.getFluid(1000))
			.fluidInputs(Ethanol.getFluid(1000))
			.fluidOutputs(FloweryEssence.getFluid(2000))
			.duration(3000).EUt(VA[ULV]).buildAndRegister();

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
	}


}
