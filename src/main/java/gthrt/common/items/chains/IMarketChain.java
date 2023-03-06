package gthrt.common.items.chains;

import gthrt.GTHRTMod;
public interface IMarketChain{

	abstract public boolean getEnable();
	default public void registerMarket(){};
	default public void registerItems(int offset){};
	default public void registerRecipes(){};
	default public void handleMaterials(int offset){};
}
