package gthrt.common;

import gthrt.GTHRTMod;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.IItemHandlerModifiable;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.common.items.MetaItems;
import gregtech.api.util.GTUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays	;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;

public class HRTUtils{
	public static final MetaItem<?>.MetaValueItem[] COINS = {MetaItems.CREDIT_CUPRONICKEL,
															MetaItems.CREDIT_SILVER,
															MetaItems.CREDIT_GOLD,
															MetaItems.CREDIT_PLATINUM,
															MetaItems.CREDIT_OSMIUM,
															MetaItems.CREDIT_NAQUADAH,
															MetaItems.CREDIT_NEUTRONIUM};

	public static <T> ArrayList<ArrayList<T>> getSubsets(T[] in){
		if(in.length >= 32){return null;}
		ArrayList<ArrayList<T>> out =  new ArrayList<ArrayList<T>>();
		for(int i=0; i< 0b1<<in.length; i++){
			ArrayList<T> x = new ArrayList<T>();
			for(int j=0;j<in.length;j++){
				if((i&0b1<<j)>0){
					x.add(in[j]);
				}
			}
			out.add(x);
		}
		GTHRTMod.logger.info("all Subset set size = {} for {}",out.size(),in);
		return out;

	}
	public static double variableRound(Float in, int places){
		return Math.round(in*Math.pow(10,places))/Math.pow(10,places);
	}
	public static double variableRound(Float in){
		return variableRound(in,2);
	}

	public static <K,V> HashMap<K,V> maskMap(Map<K,V> map, Set<K> mask){
		HashMap<K,V> out = new HashMap<K,V>();
		for(K i : mask){
			if(map.get(i)!= null){
				out.put(i,map.get(i));
			}
		}
		return out;
	}

	public static int getCreditValue(ItemStack i){
		if(Arrays.asList(COINS).contains(((MetaItem)i.getItem()).getItem(i))){return Math.toIntExact(Math.round((i.getCount()*Math.pow(8,i.getItemDamage()-1))));}
		return 0;
	}
	public static class SortCreditStacks implements Comparator<ItemStack>{
		public SortCreditStacks(){}
		public int compare(ItemStack a,ItemStack b){
			return Math.toIntExact(getCreditValue(a)-getCreditValue(b));
		}
	}
	public static class SortByBurn implements Comparator<ItemStack>{
		public SortByBurn(){}
		public int compare(ItemStack a,ItemStack b){
			return actuallyGetBurnTime(a)-actuallyGetBurnTime(b);
		}
	}
	public static List<ItemStack> creditsToCoins(int in){
		ArrayList<ItemStack> out = new ArrayList<ItemStack>();
		int x = in; //don't want to clobber the input value
		for(int i = 6;i>=0;i--){ //count down from neutronium
			if(x>>(i*3) > 64){ //should only happen at neutronium but maybe we want a max denomination at some point
				out.add(COINS[i].getStackForm(64));
				x-=64<<i*3;
				i++;//stay on the same level
				continue;
			}
			out.add(COINS[i].getStackForm(x>>(i*3)));//we're dealing with powers of 8 so this works like a truncated integer division
			x-=x>>(i*3)<<(i*3);//remove the value of the coins we just added
		}
		return out;
	}
	public static List<ItemStack> creditsToCoins(float in){return creditsToCoins(Math.round(in));}

	public static int stackListAmount(List<ItemStack> in){
		int out = 0;
		for(ItemStack i: in){
			out += i.getCount();
		}
		return out;
	}

	public static int ceilDiv(float a,float b){
		return (int)Math.ceil(a/b);
	}

	public static ItemStack copyChangeSize(ItemStack in, int amount){
		ItemStack out = in.copy();
		out.setCount(amount);
		return out;
	}

	public static List<ItemStack> itemHandlersToList(final List<IItemHandlerModifiable> in){
		List<ItemStack> out = new ArrayList<ItemStack>();
		for(IItemHandlerModifiable i : in){
			out.addAll(GTUtility.itemHandlerToList(i));
		}
		return out;
	}
	public static int actuallyGetBurnTime(ItemStack in){
		return TileEntityFurnace.getItemBurnTime(in) * in.getCount();
	}

}
