package gthrt.common.items;

import gregtech.api.items.metaitem.stats.IItemBehaviour;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import gthrt.common.market.MarketHandler;
import gthrt.common.market.Market;

import java.util.List;
import java.lang.Math;


public class MarketValueComponent implements IItemBehaviour{
	public String marketName;
	public float amount;

	public MarketValueComponent(String _marketName, float _amount){
		if(MarketHandler.marketTypes.get(_marketName) == null) return;
		marketName = _marketName;
		amount = _amount;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemStack, List<String> lines) {
		Market m = MarketHandler.markets.get(marketName);
		if(m == null){
			lines.add(marketName);
			return;
		}
		lines.add(MarketHandler.makeTooltip(marketName,amount));

		//lines.add(I18n.format("market.names."+marketName));
	}
}
