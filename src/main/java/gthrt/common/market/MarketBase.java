package gthrt.common.market;


public class MarketBase{
	public final String name;
	public final float baseValue;
	public final int scale;
	public final float volatility;
	public final float elasticity;
	public final int color;
	public MarketBase(String _name,float _baseValue,int _scale, float _volatility, float _elasticity,int _color){
		this.name=_name;
		this.baseValue=_baseValue;
		this.scale=_scale;
		this.volatility=_volatility;
		this.elasticity=_elasticity;
		this.color = _color;
	}
}
