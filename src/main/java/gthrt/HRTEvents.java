package gthrt;

import gregtech.api.GregTechAPI;

import gthrt.common.HRTMats;
import gthrt.common.HRTItems;
import gthrt.common.market.MarketHandler;
import gthrt.command.CommandMarket;
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

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid = GTHRTMod.MODID)
public class HRTEvents{
	@SubscribeEvent
	public static void onMaterialsInit(GregTechAPI.MaterialEvent event) {
		HRTMats materials = new HRTMats();
		materials.handleChains();
	}

    @SubscribeEvent
    public static void registerItems(@Nonnull RegistryEvent.Register<Item> event) {
		HRTItems.init();
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
