package gthrt.common;

import gthrt.common.items.chains.*;

public class HRTChains{

	//probably should find a more maintainable and extendable way to do this
	private static final AbstractMarketChain[] allChains = {new ClothingChain(),
															new PersonalHygieneChain()};
	public static void initItems(){
		int offset = 64; //non chain items go under here
		for( AbstractMarketChain i : allChains){
			if(i.enable){i.registerItems(offset);}
			offset += 16; //still offset so that ids don't break
		}
	}

	public static void initMarkets(){
		for(AbstractMarketChain i : allChains){
			if(i.enable){i.registerMarket();}
		}
	}
	public static void initRecipes(){
		HRTItems.HRT_PACKAGES.generateRecipes();
		for( AbstractMarketChain i : allChains){if(i.enable){i.registerRecipes();}}
	}
	public static void initMaterials(){
		int offset = 2432; //probably not gonna need a lot of materials apart from chains
		for( AbstractMarketChain i : allChains){
			if(i.enable){i.handleMaterials(offset);}
			offset += 16; //still offset so that ids don't break
		}
	}


}
