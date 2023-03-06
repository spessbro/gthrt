package gthrt.common;

import gthrt.GTHRTMod;

import net.minecraftforge.common.config.Config;

import gthrt.common.items.chains.*;

import java.util.Map;
import java.util.HashMap;

@Config(modid=GTHRTMod.MODID)
public class HRTConfig{

	@Config.Comment("Ticks per market step; 1200 ticks = 1 minute; default=6000")
	public static int ticksPerStep = 6000;
	@Config.Comment("These are the settings to enable or disable any of the chains and markets offered by this mod")
	public static boolean enableClothingChain 			= true;
	public static boolean enablePersonalHygieneChain 	= true;
	public static boolean enableKitchenwareChain		= true;

	@Config.Comment("Replace construction foam with polyurethane, a much more difficult to obtain material although more realistic default=false")
	public static boolean replaceConstructionFoam = false;

	static Map<String,Boolean> enableMap= new HashMap<String,Boolean>(){{	put(ClothingChain.MARKET_KEY,enableClothingChain);
																			put(PersonalHygieneChain.MARKET_KEY,enablePersonalHygieneChain);
																			put(KitchenwareChain.MARKET_KEY,enableKitchenwareChain);}};

	public static boolean getEnable(String key){
		return enableMap.get(key);
	}



}
