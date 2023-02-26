package gthrt.client.widget;

import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.recipes.ingredients.IntCircuitIngredient;

import net.minecraftforge.items.ItemStackHandler;
import net.minecraft.util.NonNullList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import gthrt.common.market.MarketHandler;

import java.util.Arrays;

class RemovableMarketEntryWidget extends WidgetGroup{
	static final byte CIRCUIT = 0;

	public String market;
	ItemStackHandler handler;
	public RemovableMarketEntryWidget(int x, int y, int w, int h,String name){
		super(x,y,w,h);
		market = name;
		handler = new ItemStackHandler(2);
		addWidget(new SlotWidget(handler,CIRCUIT,0,0,false,false));
		addWidget(new SlotWidget(handler,1,18,0,false,false));
		addWidget(new LabelWidget(36,4,MarketHandler.marketTypes.get(name).formatName(),MarketHandler.marketTypes.get(name).color));
	}

	public AddableMarketEntryWidget toAddable(int x,int y){
		return new AddableMarketEntryWidget(x,y,getSize().width,getSize().height,market){{setIcon();}};
	}
	public void setIcon(int circuit){
		handler.setStackInSlot(1,MarketHandler.buyMarkets.get(market).toItemStack());
		handler.setStackInSlot(CIRCUIT,IntCircuitIngredient.getIntegratedCircuit(circuit));
	}
	public void setCircuit(int circuit){
		handler.setStackInSlot(CIRCUIT,IntCircuitIngredient.getIntegratedCircuit(circuit));
	}

	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		return isVisible() && isActive() && isMouseOverElement(mouseX,mouseY);
	}
	@SideOnly(Side.CLIENT)
	public void drawInForeground(int mouseX, int mouseY){
		if(widgets.get(2).isMouseOverElement(mouseX,mouseY)){
			widgets.get(2).drawHoveringText(ItemStack.EMPTY,Arrays.asList(MarketHandler.makeTooltip(market,1f)),255,mouseX,mouseY);
		}
		else{
			super.drawInForeground(mouseX,mouseY);
		}
	}




}
