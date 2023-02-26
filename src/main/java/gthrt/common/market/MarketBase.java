package gthrt.common.market;


import net.minecraft.client.resources.I18n;

import static gregtech.api.unification.ore.OrePrefix.ore;
import static gregtech.api.GregTechAPI.MaterialRegistry;


public class MarketBase{
	public final String name;
	public final float baseValue;
	public final int scale;
	public final float volatility;
	public final int color;
	public final boolean isMaterial;
	public MarketBase(String _name,float _baseValue,int _scale, float _volatility,int _color){
		this.name=_name;
		this.baseValue=_baseValue;
		this.scale=_scale;
		this.volatility=_volatility;
		this.color = _color;
		isMaterial = false;
	}
	public MarketBase(String _name,float _baseValue,int _scale, float _volatility,int _color,boolean _isMaterial){
		this.name=_name;
		this.baseValue=_baseValue;
		this.scale=_scale;
		this.volatility=_volatility;
		this.color = _color;
		isMaterial = _isMaterial;

	}
	public String formatName(){
		return isMaterial ? ore.getLocalNameForItem(MaterialRegistry.get(name)) : I18n.format("market."+name+".name");
	}
}
