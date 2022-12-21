package gthrt.common.market;


import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

public class Market{

	public String name;
	public int baseValue;
	public int scale;
	public float volatility;
	public float elasticity;
	public int color;

	public ArrayList<int[]> supplyModifiers;
	public ArrayList<int[]> demandModifiers;
	public ArrayList<Float> valueHistory;
	public float currentValue;

	public Market(String _name,int _baseValue,int _scale, float _volatility, float _elasticity,int _color){
		name=_name;
		baseValue=_baseValue;
		scale=_scale;
		volatility=_volatility;
		elasticity=_elasticity;
		color = _color;
		supplyModifiers = new ArrayList<int[]>();
		demandModifiers = new ArrayList<int[]>();
		valueHistory = new ArrayList<Float>();
		currentValue = baseValue;
		valueHistory.add(currentValue);

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
		currentValue += ((getModifiers(false)/getModifiers(true))*baseValue-currentValue)*elasticity;
		valueHistory.add(currentValue);
		if(valueHistory.size()>20){
			valueHistory.remove(0);
		}

	}
	public int getModifiers(boolean supplyOrDemand){
		int out = scale;
		for(int[] i : supplyOrDemand ? supplyModifiers : demandModifiers){
			out += i[0];
		}
		return out;
	}
	public int[] makeModifier(Random random){
		int[] out = {Math.round((random.nextFloat()*4-2)*volatility*scale),random.nextInt(5)+1};
		return out;
	}



	public NBTTagCompound writeToNBT(){
		NBTTagCompound out = new NBTTagCompound();
		out.setIntArray("supplyMods",concatIntList(supplyModifiers));
		out.setIntArray("demandMods",concatIntList(demandModifiers));
		int[] bits = new int[valueHistory.size()];
		for(int i=0;i<valueHistory.size();i++){
			bits[i]=Float.floatToIntBits(valueHistory.get(i));
		}
		out.setIntArray("valueHistory",bits);
		return out;

	}
	public static Market readFromNBT(NBTTagCompound in,String baseData){
		Market out = parseFromString(baseData);
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
	public static Market parseFromString(String in){
		String[] data = in.split("\\|", -1);
		return new Market(data[0],Integer.parseInt(data[1]),Integer.parseInt(data[2]),Float.parseFloat(data[3]),Float.parseFloat(data[4]),Integer.parseInt(data[5],16));
	}

	private static int[] concatIntList(ArrayList<int[]> in){
		int[] out = new int[in.size()*2];
		for(int i=0;i<in.size();i++){
			out[i*2]=in.get(i)[0];
			out[i*2+1]=in.get(i)[1];
		}
		return out;
	}
}
