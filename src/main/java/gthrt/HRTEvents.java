package gthrt;

import gregtech.api.GregTechAPI;

import gthrt.common.HRTMats;
import gthrt.common.items.HRTItems;
import gthrt.common.HRTChains;
import gthrt.common.market.MarketHandler;
import gthrt.command.CommandMarket;
import gthrt.common.block.HRTBlocks;
import gthrt.common.market.MarketPacket;
import gthrt.common.market.MarketData;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import com.google.common.eventbus.Subscribe;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.block.Block;
import net.minecraft.util.NonNullList;


import gregtech.api.unification.material.Materials;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.blocks.MetaBlocks;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.recipes.GTRecipeHandler;
import gregtech.common.items.MetaItems;
import gregtech.api.recipes.ModHandler;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.api.GTValues;
import static gregtech.api.recipes.RecipeMaps.FORMING_PRESS_RECIPES;
import gthrt.common.HRTTiles;
import gthrt.common.block.HRTBlocks;
import net.minecraft.item.ItemStack;


import javax.annotation.Nonnull;

import gregtech.core.CoreModule;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid = GTHRTMod.MODID)
public class HRTEvents{
	@SubscribeEvent
	public static void onMaterialsInit(GregTechAPI.MaterialEvent event) {
		HRTMats materials = new HRTMats();
		materials.handleChains();
	}
	@SubscribeEvent
  	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		HRTTiles.init();
		HRTBlocks.init(event);
  	}
    @SubscribeEvent
    public static void registerItems(@Nonnull RegistryEvent.Register<Item> event) {
		HRTItems.init();
		HRTChains.initItems();
		HRTBlocks.registerBlockItems(event);
    }
    @SubscribeEvent
    public static void registerRecipes(@Nonnull RegistryEvent.Register<IRecipe> event) {
		HRTItems.HRT_PACKAGES.generateRecipes();
		HRTChains.initRecipes();
		HRTTiles.initRecipes();
		HRTBlocks.initRecipes();
		GTRecipeHandler.removeRecipesByInputs(FORMING_PRESS_RECIPES,MetaItems.SHAPE_MOLD_CREDIT.getStackForm(),OreDictUnifier.get(OrePrefix.plate, Materials.Cupronickel));

    }

    @SubscribeEvent
    public static void onWorldUnloadEvent(WorldEvent.Unload event) {
        MarketData.setDirty();
    }

    @SubscribeEvent
    public static void onWorldSaveEvent(WorldEvent.Save event) {
        MarketData.setDirty();
    }
    @SubscribeEvent
    public static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
    	if (!event.player.getEntityWorld().isRemote && event.player instanceof EntityPlayerMP) {
			GregTechAPI.networkHandler.sendTo(new MarketPacket(MarketHandler.markets),(EntityPlayerMP) event.player);
    	}
    }
}
