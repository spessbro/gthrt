package gthrt.common.items.chains;

import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

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
import gthrt.common.HRTConfig;



public class ClothingChain implements IMarketChain{
	public static final String MARKET_KEY = "clothing";
	public static MetaItem<?>.MetaValueItem CLOTH;
	public static MetaItem<?>.MetaValueItem PANTS;
	public static MetaItem<?>.MetaValueItem SHIRT;
	public static MetaItem<?>.MetaValueItem SOLE;
	public static MetaItem<?>.MetaValueItem SHOES;

	public boolean getEnable(){
		return HRTConfig.getEnable(MARKET_KEY);//boilerplate but I don't see another way
	}

	public void registerMarket(){
    		MarketHandler.defineSellMarket(new MarketBase(MARKET_KEY,
                                                          23,4000,
                                                          0.1f,
                                                          0x253b9f));
    }

	public void registerItems(int offset){
		boolean isEnable = getEnable();
		SOLE =HRTItems.HRT_ITEMS.addItem(offset,"sole").setInvisible(isEnable);
		CLOTH=HRTItems.HRT_ITEMS.addItem(offset+1,"cloth").setInvisible(isEnable);
		PANTS=HRTItems.addMarketItem(offset+2,"pants",MARKET_KEY,0.1f,isEnable);
		SHIRT=HRTItems.addMarketItem(offset+3,"shirt",MARKET_KEY,0.1f,isEnable);
		SHOES=HRTItems.addMarketItem(offset+4,"shoes",MARKET_KEY,0.15f,isEnable);

		//handle vanilla item values
		if(isEnable){
			MarketHandler.makeSellable(new ItemStack(Items.LEATHER_BOOTS),MARKET_KEY,0.1f);
			MarketHandler.makeSellable(new ItemStack(Items.LEATHER_LEGGINGS),MARKET_KEY,0.15f);
			MarketHandler.makeSellable(new ItemStack(Items.LEATHER_CHESTPLATE),MARKET_KEY,0.1f);
		}
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
		FORGE_HAMMER_RECIPES.recipeBuilder()
			.input(Items.LEATHER,2)
			.output(SOLE)
			.duration(160).EUt(VA[ULV]).buildAndRegister();
		FORGE_HAMMER_RECIPES.recipeBuilder()
			.input(OrePrefix.plate,Materials.Rubber)
			.output(SOLE)
			.duration(80).EUt(VA[ULV]).buildAndRegister();
		CUTTER_RECIPES.recipeBuilder()
			.input(OrePrefix.ingot,Materials.Rubber,4)
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


	}




}
