package gthrt.common.market;


import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;


import gthrt.GTHRTMod;

public class Market extends MarketBase{

	public ArrayList<int[]> supplyModifiers;
	public ArrayList<int[]> demandModifiers;
	public ArrayList<Float> valueHistory;

	public Market(String _name,float _baseValue,int _scale, float _volatility,int _color, boolean _isMaterial){
		super(_name,_baseValue,_scale,_volatility,_color, _isMaterial);
		supplyModifiers = new ArrayList<int[]>();
		demandModifiers = new ArrayList<int[]>();
		valueHistory = new ArrayList<Float>();
		valueHistory.add(_baseValue);
	}
	public MarketBase toBase(){
    	return new MarketBase(name,baseValue,scale,volatility,color,isMaterial);
    }


	public static Market fromBase(MarketBase in){
		return new Market(in.name,in.baseValue,in.scale,in.volatility,in.color,in.isMaterial);
	}


	public void Step(Random random){
		valueHistory.add(getValue());
		if(valueHistory.size()>=20){
			valueHistory.remove(0);
		}
		for(int i=0;i<supplyModifiers.size();i++){
			int[] target = supplyModifiers.get(i);
			target[1]--;
			if(target[1]==0){
				supplyModifiers.remove(i);
				i--;
			}
		}
		for(int i=0;i<demandModifiers.size();i++){
			int[] target = demandModifiers.get(i);
			target[1]--;
			if(target[1]==0){
				demandModifiers.remove(i);
				i--;
			}
		}
		if(random.nextFloat()<volatility){
			(random.nextBoolean() ? demandModifiers : supplyModifiers).add(makeModifier(random));
		}

	}
	public float getDemandModifiers(){
		float out = scale;
		for(int[] i : demandModifiers){
			out += i[0];
		}
		return out;
	}
	public float getSupplyModifiers(){
		float out = scale;
		for(int[] i : supplyModifiers){
			out += i[0];
		}
		return out;
	}
	public int[] makeModifier(Random random){
		int[] out = {Math.toIntExact(Math.round(random.nextFloat()*volatility*scale)),random.nextInt(3)+1};
		return out;
	}



	public NBTTagCompound writeToNBT(){
		NBTTagCompound out = new NBTTagCompound();
		out.setIntArray("supplyMods",concatIntList(this.supplyModifiers));
		out.setIntArray("demandMods",concatIntList(this.demandModifiers));
		int[] bits = new int[valueHistory.size()];
		for(int i=0;i<this.valueHistory.size();i++){
			bits[i]=Float.floatToIntBits(this.valueHistory.get(i));
		}
		out.setIntArray("valueHistory",bits);
		return out;

	}
	public static Market readFromNBT(NBTTagCompound in,MarketBase base){
		if(in.getKeySet().isEmpty()){
			GTHRTMod.logger.error("Tried to parse invalid market nbt");
			return null;}
		Market out = fromBase(base);

		int[] supply = in.getIntArray("supplyMods");
		for(int i =0;i<supply.length;i+=2){
			out.supplyModifiers.add(new int[] {supply[i],supply[i+1]});
		}
		int[] demand = in.getIntArray("demandMods");
		for(int i =0;i<demand.length;i+=2){
			out.demandModifiers.add(new int[] {demand[i],demand[i+1]});
		}
		int[] history = in.getIntArray("valueHistory");
		for(int i=0;i<history.length;i++){
			out.valueHistory.add(Float.intBitsToFloat(history[i]));
		}
		return out;
	}


	private static int[] concatIntList(ArrayList<int[]> in){
		int[] out = new int[in.size()*2];
		for(int i=0;i<in.size();i++){
			out[i*2]=in.get(i)[0];
			out[i*2+1]=in.get(i)[1];
		}
		return out;
	}

	public float getChange(){
		if(valueHistory.size()<1){return 0f;}
		return (getValue()/valueHistory.get(valueHistory.size()-1))-1f;
	}
	public float getValue(){
		return (float) Math.sqrt(getDemandModifiers()/getSupplyModifiers()) * baseValue;
	}

	public float approxBuyValue(int count){
		return (float)Math.sqrt((getDemandModifiers()+count/2)/getSupplyModifiers())*count*baseValue;
	}

	public float approxSellValue(float count){
		return (float)Math.sqrt(getDemandModifiers()/(getSupplyModifiers()+count/2))*count*baseValue;
	}

	public int approxBuyCount(float value,float freight){
		float x = 0;
		int i = 0;
		while(x+Math.sqrt((getDemandModifiers()+i)/getSupplyModifiers())*baseValue+freight < value){
			x+= Math.sqrt((getDemandModifiers()+i)/getSupplyModifiers())*baseValue+freight;
			i++;
		}
		return i;
	}
}
