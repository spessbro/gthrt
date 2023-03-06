package gthrt.common;

import gthrt.common.items.chains.*;
import gthrt.GTHRTMod;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;


public class HRTChains{

	//probably should find a more maintainable and extendable way to do this
	private static final IMarketChain[] allChains = {new ClothingChain(),
													 new PersonalHygieneChain(),
													 new KitchenwareChain()};
	public static void initItems(){
		int offset = 64; //non chain items go under here
		for(IMarketChain i : allChains){
			i.registerItems(offset); //always register items, just hide them
			offset += 16;
		}
	}

	public static void initMarkets(){
		for(IMarketChain i : allChains){
			i.registerMarket();//TODO:Find a workaround to not break saves
		}
	}
	public static void initRecipes(){
		for(IMarketChain i : allChains){
			if(i.getEnable()){i.registerRecipes();}
		}
	}
	public static void initMaterials(){
		int offset = 2432; //probably not gonna need a lot of materials apart from chains
		for(IMarketChain i : allChains){
			i.handleMaterials(offset);//same as items but not hiding stuff created TODO
			offset += 16;
		}
	}


}
