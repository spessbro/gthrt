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
import gregtech.common.items.MetaItems;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemComponent;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

import gthrt.GTHRTMod;
import gthrt.common.HRTUtils;
import gthrt.common.HRTConfig;
import gthrt.common.items.MarketValueComponent;



import gregtech.api.GregTechAPI;

public class MarketHandler{
	private static final String DATA_NAME = GTHRTMod.MODID + "_MarketData";
	public static Map<String,MarketBase> marketTypes = new HashMap<String,MarketBase>();
	public static Map<String,Market> markets = new HashMap<String,Market>();

	public static final boolean BUY = true;
	public static Map<String,ItemAndMetadata> buyMarkets = new HashMap<String,ItemAndMetadata>();
	public static final boolean SELL = false;
	public static Set<String> sellMarkets = new HashSet<String>();

	public static int ticks = 1;

	public static final ItemAndMetadata airItem =  new ItemAndMetadata(Items.AIR,0);

	public static Map<ItemAndMetadata, Pair<String,Float>> sellableItems = new HashMap<ItemAndMetadata, Pair<String,Float>>();

	//putting this here for now
	public static void populateMarkets(){
		defineBuyMarket(new MarketBase("rubber",1,3000,0.1f,0.1f,0xffcc42),MetaItems.STICKY_RESIN.getStackForm());
	}
	public static void makeSellable(ItemStack item, String marketName, float value){
		sellableItems.put(new ItemAndMetadata(item),new ImmutablePair<String,Float>(marketName, value));
	}

	public static void makeBuyable(ItemStack item, String marketName){
		if(!buyMarkets.containsKey(marketName)){
			GTHRTMod.logger.error("Tried to set buyable to invalid market");
			return;
		}
		buyMarkets.put(marketName,new ItemAndMetadata(item));

	}


	public static void defineSellMarket(MarketBase in){
		marketTypes.put(in.name,in);
		sellMarkets.add(in.name);
	}

	public static void defineBuyMarket(MarketBase in,ItemAndMetadata item){
		marketTypes.put(in.name,in);
		buyMarkets.put(in.name,item);
	}
	public static void defineBuyMarket(MarketBase in){
		defineBuyMarket(in, airItem); //In case we generate the market before the sellable item.
	}
	public static void defineBuyMarket(MarketBase in,ItemStack stack){
		marketTypes.put(in.name,in);
		buyMarkets.put(in.name,new ItemAndMetadata(stack));
	}

	public static Map<String,Market> getMarkets(boolean buyOrSell){
		return HRTUtils.maskMap(markets, buyOrSell ? buyMarkets.keySet() : sellMarkets);
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
									m.getChange() == 0f ? "=" : m.getChange()>0 ? "▲" : "▼",HRTUtils.variableRound(Math.abs(m.getChange())*100),
									HRTUtils.variableRound(m.currentValue*value));
	}
	public static String makeTooltip(Map.Entry<String,Float> in){
		return makeTooltip(in.getKey(),in.getValue());
	}
	public static Map.Entry<String,Float> getValue(ItemStack i){
		if(markets.size() == 0){return null;}
		if(i.getItem() instanceof MetaItem){
			GTHRTMod.logger.info("ItemStack {} is MetaItem with stats {} long",i,((MetaItem) i.getItem()).getItem(i).getAllStats().size());
			for(IItemComponent c :(List<IItemComponent>) ((MetaItem) i.getItem()).getItem(i).getAllStats()){

				if(c instanceof MarketValueComponent){return new ImmutablePair<String,Float>(((MarketValueComponent)c).marketName,((MarketValueComponent)c).amount*i.getCount());}
			}
			return null;
		}
		else{
			return sellableItems.get(new ItemAndMetadata(i));

		}
	}
}
