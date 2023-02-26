package gthrt.common;

import gthrt.common.items.chains.*;
import gthrt.GTHRTMod;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;


public class HRTChains{

	//probably should find a more maintainable and extendable way to do this
	private static final AbstractMarketChain[] allChains = {new ClothingChain(),
															new PersonalHygieneChain()};
	public static void initItems(){
		int offset = 64; //non chain items go under here
		for( AbstractMarketChain i : allChains){
			if(i.getEnable()){i.registerItems(offset);}
			offset += 16; //still offset so that ids don't break
		}
	}

	public static void initMarkets(){
		for(AbstractMarketChain i : allChains){
			if(i.getEnable()){i.registerMarket();}
		}
	}
	public static void initRecipes(){
		for( AbstractMarketChain i : allChains){
			if(i.getEnable()){i.registerRecipes();}
		}
	}
	public static void initMaterials(){
		int offset = 2432; //probably not gonna need a lot of materials apart from chains
		for( AbstractMarketChain i : allChains){
			if(i.getEnable()){i.handleMaterials(offset);}
			offset += 16; //still offset so that ids don't break
		}
	}


}
