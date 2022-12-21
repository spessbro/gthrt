package gthrt.common.market;

import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Random;
import javax.annotation.Nonnull;

import gthrt.GTHRTMod;
import gthrt.common.HRTConfig;


import gregtech.api.GregTechAPI;

public class MarketHandler{
	private static final String DATA_NAME = GTHRTMod.MODID + "_MarketData";

	public static Map<String,Market> markets = new LinkedHashMap<String,Market>();
	public static int ticks = 0;

	public static void populateMarkets(){
		GTHRTMod.logger.info("Attempting to populate");
		for(String m : HRTConfig.MarketTypes){
			Market parsed = Market.parseFromString(m);
			markets.put(parsed.name,parsed);
		}
	}
	@SubscribeEvent
	public static void onTick(TickEvent.WorldTickEvent event){
		if(!event.world.isRemote){
			if(ticks>HRTConfig.ticksPerStep){
				ticks=0;
				doStep(event.world.rand);
			}
			ticks++;
		}
	}

	public static void doStep(Random random){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			for(String m : markets.keySet()){
				markets.get(m).Step(random);
			}

			GregTechAPI.networkHandler.sendToAll(new MarketPacket(markets));
			MarketData.setDirty();
		}
	}

}
