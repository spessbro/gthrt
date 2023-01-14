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
	public float currentValue;

	public Market(String _name,float _baseValue,int _scale, float _volatility, float _elasticity,int _color){
		super(_name,_baseValue,_scale,_volatility,_elasticity,_color);
		supplyModifiers = new ArrayList<int[]>();
		demandModifiers = new ArrayList<int[]>();
		valueHistory = new ArrayList<Float>();
		currentValue = baseValue;
		valueHistory.add(currentValue);
	}
	public MarketBase toBase(){
    	return new MarketBase(name,baseValue,scale,volatility,elasticity,color);
    }


	public static Market fromBase(MarketBase in){
		GTHRTMod.logger.info("Turning base {} into market", in.name);
		return new Market(in.name,in.baseValue,in.scale,in.volatility,in.elasticity,in.color);
	}


	public void Step(Random random){
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
		currentValue += ((getModifiers(false)/getModifiers(true)+1)*baseValue-currentValue)*elasticity;
		valueHistory.add(currentValue);
		if(valueHistory.size()>=20){
			valueHistory.remove(0);
		}

	}
	public int getModifiers(boolean supplyOrDemand){
		int out = scale;
		for(int[] i : supplyOrDemand ? supplyModifiers : demandModifiers){
			out += i[0];
			if(out==0){out=(int) Math.round(scale*0.1);break;}
		}
		return out;
	}
	public int[] makeModifier(Random random){
		int[] out = {Math.toIntExact(Math.round((random.nextFloat()-0.5)*volatility*scale)),random.nextInt(2)+1};
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
		GTHRTMod.logger.info("Writing Market to NBT >> {}", out.toString());
		return out;

	}
	public static Market readFromNBT(NBTTagCompound in,MarketBase base){
		if(in.getKeySet().isEmpty()){
			GTHRTMod.logger.error("Tried to parse invalid market nbt");
			return null;}
		GTHRTMod.logger.info("Reading Market to NBT >> {}", in);
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
		out.currentValue=out.valueHistory.get(history.length);
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
		if(valueHistory.size()<2){return 0f;}
		return (currentValue/valueHistory.get(valueHistory.size()-2))-1f;
	}
}
