package gthrt.common.items.chains;

import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;

import gregtech.api.recipes.ModHandler;
import gregtech.common.items.MetaItems;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.stack.UnificationEntry;
import static gregtech.api.GTValues.*;
import static gregtech.api.recipes.RecipeMaps.*;

import gthrt.common.market.MarketHandler;
import gthrt.common.market.MarketBase;
import gthrt.common.items.HRTItems;
import gthrt.GTHRTMod;



@Config(modid=GTHRTMod.MODID)
public class ClothingChain extends AbstractMarketChain{
	@Config.Ignore
	private static final String MARKET_KEY = "clothing";

	@Config.Name("enable"+MARKET_KEY+"chain")
	private boolean enable = true;
	public boolean getEnable(){return enable;};
	@Config.Ignore
	public static MetaItem<?>.MetaValueItem CLOTH;
	@Config.Ignore
	public static MetaItem<?>.MetaValueItem PANTS;
	@Config.Ignore
	public static MetaItem<?>.MetaValueItem SHIRT;
	@Config.Ignore
	public static MetaItem<?>.MetaValueItem SOLE;
	@Config.Ignore
	public static MetaItem<?>.MetaValueItem SHOES;

	public void registerMarket(){
    		MarketHandler.defineSellMarket(new MarketBase(MARKET_KEY,
                                                          23,4000,
                                                          0.1f,
                                                          0x253b9f));
    }

	public void registerItems(int offset){
		SOLE =HRTItems.HRT_ITEMS.addItem(offset,"sole");
		CLOTH=HRTItems.HRT_ITEMS.addItem(offset+1,"cloth");
		PANTS=HRTItems.addMarketItem(offset+2,"pants",MARKET_KEY,0.1f);
		SHIRT=HRTItems.addMarketItem(offset+3,"shirt",MARKET_KEY,0.15f);
		SHOES=HRTItems.addMarketItem(offset+4,"shoes",MARKET_KEY,0.1f);

		//handle vanilla item values
		MarketHandler.makeSellable(new ItemStack(Items.LEATHER_BOOTS),MARKET_KEY,0.1f);
		MarketHandler.makeSellable(new ItemStack(Items.LEATHER_LEGGINGS),MARKET_KEY,0.15f);
		MarketHandler.makeSellable(new ItemStack(Items.LEATHER_CHESTPLATE),MARKET_KEY,0.1f);

	}
	public void registerRecipes(){
		//change vanilla boots recipe
		ModHandler.removeRecipeByName(new ResourceLocation("minecraft:leather_boots"));
		ModHandler.addShapedRecipe("leather_boots", new ItemStack(Items.LEATHER_BOOTS), new Object[] {
			"LxL",
			"SrS",
		    Character.valueOf('L'), new ItemStack(Items.LEATHER),
		    Character.valueOf('S'), SOLE.getStackForm()});
		//Soles
		ModHandler.addShapedRecipe("manual_rubber_soles", SOLE.getStackForm(2), new Object[] {
			"sI",
			"If",
			Character.valueOf('I'),new UnificationEntry(OrePrefix.ingot,Materials.Rubber)});//please use rubber
		ModHandler.addShapedRecipe("manual_leather_soles", SOLE.getStackForm(), new Object[] {
			"sL",
			"Lf",
			Character.valueOf('L'),new ItemStack(Items.LEATHER)});
		COMPRESSOR_RECIPES.recipeBuilder()
			.input(Items.LEATHER,2)
			.output(SOLE)
			.duration(160).EUt(VA[ULV]).buildAndRegister();
		COMPRESSOR_RECIPES.recipeBuilder()
			.input(OrePrefix.ingot,Materials.Rubber)
			.output(SOLE)
			.duration(80).EUt(VA[ULV]).buildAndRegister();
		CUTTER_RECIPES.recipeBuilder()
			.input(OrePrefix.plate,Materials.Rubber,4)
			.output(SOLE,6)
			.duration(160).EUt(VA[LV]).buildAndRegister();
		//Cloth
		ModHandler.addShapedRecipe("manual_cloth", CLOTH.getStackForm(), new Object[] {
			"SSS",
			"SFS",
			"SSS",
			Character.valueOf('F'), MetaItems.WOODEN_FORM_EMPTY.getStackForm(),
			Character.valueOf('S'), new ItemStack(Items.STRING)});
		ASSEMBLER_RECIPES.recipeBuilder()
			.input(Items.STRING,4)
			.notConsumable(MetaItems.WOODEN_FORM_EMPTY)
			.output(CLOTH)
			.duration(480).EUt(VA[LV]).buildAndRegister();
			//Or make felt with a compressor and some wool
		COMPRESSOR_RECIPES.recipeBuilder()
			.input(Blocks.WOOL,3)
			.output(CLOTH)
			.duration(360).EUt(VA[ULV]).buildAndRegister();

		//Shoes (Someone please build a sweatshop)
		ModHandler.addShapedRecipe("shoes", SHOES.getStackForm(), new Object[] {
			"CxC",
			"S S",
			Character.valueOf('C'), CLOTH.getStackForm(),
			Character.valueOf('S'), SOLE.getStackForm()});
		ASSEMBLER_RECIPES.recipeBuilder()
			.input(CLOTH,2)
			.input(SOLE,2)
			.output(SHOES)
			.duration(160).EUt(VA[LV]).buildAndRegister();
		//Pants
		ModHandler.addShapedRecipe("pants", PANTS.getStackForm(), new Object[] {
			"CCC",
			"CxC",
			"C C",
			Character.valueOf('C'), CLOTH.getStackForm()});
		ASSEMBLER_RECIPES.recipeBuilder()
			.input(CLOTH,7)
			.output(PANTS,2)
			.circuitMeta(2)
			.duration(320).EUt(VA[LV]).buildAndRegister();
		//Shirt
		ModHandler.addShapedRecipe("shirt", SHIRT.getStackForm(), new Object[] {
			"CxC",
			"CCC",
			"CCC",
			Character.valueOf('C'), CLOTH.getStackForm()});
		ASSEMBLER_RECIPES.recipeBuilder()
			.input(CLOTH,4)
			.circuitMeta(1)
			.output(SHIRT)
			.duration(160).EUt(VA[LV]).buildAndRegister();
		//Package
		ASSEMBLER_RECIPES.recipeBuilder()
			.input(SHIRT,2)
			.input(PANTS,2)
			.input(SHOES)
			.output(HRTItems.HRT_PACKAGES.getItem(MARKET_KEY+"_package"))
			.duration(200).EUt(VA[LV]).buildAndRegister();


	}




}
