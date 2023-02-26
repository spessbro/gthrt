package gthrt.client.widget;

import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.gui.widgets.LabelWidget;

import net.minecraftforge.items.ItemStackHandler;
import net.minecraft.util.NonNullList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import gthrt.common.market.MarketHandler;
import gthrt.GTHRTMod;

import java.util.Arrays;

class AddableMarketEntryWidget extends WidgetGroup{
	public String market;
	ItemStackHandler handler;
	public AddableMarketEntryWidget(int x, int y, int w, int h, String name){
		super(x,y,w,h);
		market = name;
		handler = new ItemStackHandler(1);
		addWidget(new SlotWidget(handler,0,0,0,false,false));
		addWidget(new LabelWidget(18,4,MarketHandler.marketTypes.get(name).formatName(),MarketHandler.marketTypes.get(name).color));
	}

	public void setIcon(){
		handler.setStackInSlot(0,MarketHandler.buyMarkets.get(market).toItemStack());
	}

	public RemovableMarketEntryWidget toRemovable(int x, int y,int id){
		return new RemovableMarketEntryWidget(x,y,getSize().width,getSize().height,market){{setIcon(id);}};
	}
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		return isVisible() && isActive() && isMouseOverElement(mouseX,mouseY);
	}
	@SideOnly(Side.CLIENT)
	public void drawInForeground(int mouseX, int mouseY){
		if(widgets.get(1).isMouseOverElement(mouseX,mouseY)){
			widgets.get(1).drawHoveringText(ItemStack.EMPTY,Arrays.asList(MarketHandler.makeTooltip(market,1f)),255,mouseX,mouseY);
		}
		else{
			super.drawInForeground(mouseX,mouseY);
		}
	}


}
