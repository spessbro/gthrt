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
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.resources.I18n;

import gregtech.api.unification.stack.ItemAndMetadata;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Random;
import javax.annotation.Nonnull;
import java.util.AbstractMap;

import gthrt.GTHRTMod;
import gthrt.common.HRTConfig;



import gregtech.api.GregTechAPI;

public class MarketHandler{
	private static final String DATA_NAME = GTHRTMod.MODID + "_MarketData";
	public static Map<String,MarketBase> marketTypes = new LinkedHashMap<String,MarketBase>();
	public static Map<String,Market> markets = new LinkedHashMap<String,Market>();
	public static int ticks = 1;

	public static Map<ItemAndMetadata, Map.Entry<String,Float>> sellableItems = new LinkedHashMap<ItemAndMetadata, Map.Entry<String,Float>>();

	//putting this here for now
	public static void populateMarkets(){
		defineMarket(new MarketBase("personalhygiene",1,1000,0.5f,0.5f,0x1466CD));
		defineMarket(new MarketBase("explosives",3,400,0.8f,0.3f,0xbc1827));
	}
	public static void handleItems(){
		makeSellable(new ItemStack(Blocks.TNT),"explosives", 0.05f);
	}
	public static void makeSellable(ItemStack item, String marketName, float value){
		if(!marketTypes.containsKey(marketName)){
			GTHRTMod.logger.error("Tried to add sellable to invalid market");
			return;
		};
		sellableItems.put(new ItemAndMetadata(item),new AbstractMap.SimpleEntry<String,Float>(marketName, value));
	}



	public static void defineMarket(MarketBase in){
		marketTypes.put(in.name,in);
	}

	@SubscribeEvent
	public static void onTick(TickEvent.WorldTickEvent event){
		if(!event.world.isRemote){
			if(ticks % HRTConfig.ticksPerStep == 0){
				ticks=0;
				doStep(event.world.rand);
			}
			ticks++;
		}
	}
	public static void doStep(Random random){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			GTHRTMod.logger.info("Stepping {} markets", markets.keySet().size());
			for(String m : markets.keySet()){
				markets.get(m).Step(random);
			}

			GregTechAPI.networkHandler.sendToAll(new MarketPacket(markets));
			MarketData.setDirty();
		}
	}
	public static String makeTooltip(String marketName, float value){
	Market m = markets.get(marketName);
	if(m==null){return marketName;}
	return I18n.format("market.tooltip",m.getChange() == 0f ? TextFormatting.GRAY : m.getChange()>0 ? TextFormatting.GREEN : TextFormatting.RED,
									value,I18n.format("market.names."+marketName),
									m.getChange() == 0f ? "=" : m.getChange()>0 ? "▲" : "▼",Math.abs(m.getChange())*100,
									m.currentValue*value);
	}
	public static String makeTooltip(Map.Entry<String,Float> in){
		return makeTooltip(in.getKey(),in.getValue());
	}

}
