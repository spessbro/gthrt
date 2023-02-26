package gthrt.common.items.chains;

import gthrt.GTHRTMod;
public abstract class AbstractMarketChain{

	public boolean getEnable(){return false;}
	public void registerMarket(){}
	public void registerItems(int offset){}
	public void registerRecipes(){}
	public void handleMaterials(int offset){}


}
