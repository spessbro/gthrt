package gthrt.common.items.chains;

import gthrt.GTHRTMod;
public abstract class AbstractMarketChain{
	public static boolean enable = false;

	public static void registerMarket(){};
	public static void registerItems(int offset){};
	public static void registerRecipes(){};
	public static void handleMaterials(int offset){};


}
