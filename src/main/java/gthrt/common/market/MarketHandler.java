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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

import gregtech.api.unification.stack.ItemAndMetadata;
import gregtech.common.items.MetaItems;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemComponent;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.Material;
import static gregtech.api.unification.material.properties.PropertyKey.ORE;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

import gregtechfoodoption.item.GTFOMetaItem;

import gthrt.GTHRTMod;
import gthrt.common.HRTUtils;
import gthrt.common.HRTConfig;
import gthrt.common.items.MarketValueComponent;




import gregtech.api.GregTechAPI;
@Mod.EventBusSubscriber(modid = GTHRTMod.MODID)
public class MarketHandler{
	private static Random randomHandler = new Random();

	private static final String DATA_NAME = GTHRTMod.MODID + "_MarketData";
	public static Map<String,MarketBase> marketTypes = new HashMap<String,MarketBase>();
	public static Map<String,Market> markets = new HashMap<String,Market>();

	public static final boolean BUY = true;
	public static Map<String,ItemAndMetadata> buyMarkets = new HashMap<String,ItemAndMetadata>();
	public static final boolean SELL = false;
	public static List<String> sellMarkets = new ArrayList<String>();

	public static int ticks = 1;

	public static final ItemAndMetadata airItem =  new ItemAndMetadata(Items.AIR,0);

	public static Map<ItemAndMetadata, Pair<String,Float>> sellableItems = new HashMap<ItemAndMetadata, Pair<String,Float>>();

	//putting this here for now
	public static void populateMarkets(){
		fromOre(Materials.Coal,2,8600,0.1f);
		fromOre(Materials.GarnetSand,24,2000,0.3f);
		fromOre(Materials.Tantalite,36,1400,0.3f);
		fromOre(Materials.Bauxite,17,3500,0.2f);
		fromOre(Materials.Apatite,6,4200,0.3f);
		fromOre(Materials.Redstone,2,9000,0.3f);
		fromOre(Materials.Pentlandite,20,2500,0.15f);
		fromOre(Materials.Tetrahedrite,12,5000,0.3f);
		fromOre(Materials.Powellite,56,800,0.5f);
		fromOre(Materials.Cobaltite,26,1000,0.4f);
		fromOre(Materials.Cassiterite,8,4200,0.1f); //these are as preliminary as it gets


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


	public static MarketBase defineSellMarket(MarketBase in){
		marketTypes.put(in.name,in);
		sellMarkets.add(in.name);
		return in;
	}

	public static MarketBase defineBuyMarket(MarketBase in,ItemAndMetadata item){
		marketTypes.put(in.name,in);
		buyMarkets.put(in.name,item);
		return in;
	}
	public static MarketBase defineBuyMarket(MarketBase in){
		return defineBuyMarket(in, airItem); //In case we generate the market before the sellable item.
	}
	public static MarketBase defineBuyMarket(MarketBase in,ItemStack stack){
		return defineBuyMarket(in,new ItemAndMetadata(stack));
	}

	public static Map<String,Market> getMarkets(boolean buyOrSell){
		return HRTUtils.maskMap(markets, buyOrSell ? buyMarkets.keySet() : new HashSet<String>(sellMarkets));
	}

	@SubscribeEvent
	public static void onTick(TickEvent.WorldTickEvent event){
		if(!event.world.isRemote && event.phase == TickEvent.Phase.END && event.side == Side.SERVER){
			if(ticks % HRTConfig.ticksPerStep == 0){
				ticks=0;
				doStep();
			}
			ticks++;
		}
	}
	public static void doStep(){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			for(Market m : markets.values()){
				m.Step(randomHandler);
			}
			GregTechAPI.networkHandler.sendToAll(new MarketPacket(markets));
			MarketData.setDirty();
		}
	}
	public static String makeTooltip(String marketName, float value){
	Market m = markets.get(marketName);
	MarketBase mb = marketTypes.get(marketName);
	if(m==null && mb !=null){return mb.formatName();}
	return I18n.format("market.tooltip",m.getChange() == 0f ? TextFormatting.GRAY : m.getChange()>0 ? TextFormatting.GREEN : TextFormatting.RED,
									value,m.formatName(),
									m.getChange() == 0f ? "=" : m.getChange()>0 ? "▲" : "▼",HRTUtils.variableRound(Math.abs(m.getChange())*100),
									HRTUtils.variableRound(m.getValue()*value));
	}
	public static String makeTooltip(Map.Entry<String,Float> in){
		return makeTooltip(in.getKey(),in.getValue());
	}
	public static Pair<String,Float> getValue(ItemStack i){
		if(markets.size() == 0 || i.isEmpty()){return null;}
		if(i.getItem() instanceof MetaItem){
			for(IItemComponent c :(List<IItemComponent>) ((MetaItem) i.getItem()).getItem(i).getAllStats()){

				if(c instanceof MarketValueComponent){return ImmutablePair.of(((MarketValueComponent)c).marketName,((MarketValueComponent)c).amount*i.getCount());}
			}
			return null;
		}
		else{
			return sellableItems.get(new ItemAndMetadata(i));

		}
	}
	public static MarketBase fromOre(Material m,float baseValue,int scale,float volatility){
		if(!m.hasProperty(ORE)){
			throw new IllegalArgumentException(String.format("Material {} is not an ore",m));
		}
		return defineBuyMarket(new MarketBase(m.toString(),baseValue,scale,volatility,m.getMaterialRGB(),true),OreDictUnifier.get(OrePrefix.crushed,m));
	}

}
