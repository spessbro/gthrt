package gthrt.client.widget;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.terminal.gui.widgets.DraggableScrollableWidgetGroup;
import gregtech.api.gui.Widget;

import gthrt.GTHRTMod;
import gthrt.common.port.MetaTileEntityPortControllerAbstract;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import net.minecraft.item.ItemStack;
import gregtech.api.recipes.ingredients.IntCircuitIngredient;
import gregtech.api.gui.IRenderContext;
import static gregtech.api.gui.GuiTextures.BACKGROUND;
import gregtech.api.terminal.os.TerminalTheme;
import gregtech.api.gui.widgets.ImageWidget;
import gregtech.api.gui.widgets.LabelWidget;

import java.util.Set;
import java.util.List;

public class SwitchableMarketListWidget extends WidgetGroup{

	MetaTileEntityPortControllerAbstract port;

	DraggableScrollableWidgetGroup availableMarketsWidget;
	DraggableScrollableWidgetGroup addedMarketsWidget;

	boolean triedFix = false;

	public SwitchableMarketListWidget(int x,int y, MetaTileEntityPortControllerAbstract _port){
		port = _port;
		addWidget(new ImageWidget(130,24,90,112,BACKGROUND));
		addWidget(new LabelWidget(175,12,"label.markets_available").setXCentered(true));
		availableMarketsWidget = new DraggableScrollableWidgetGroup(133,28,87,106){{setYScrollBarWidth(3);setYBarStyle(null, TerminalTheme.COLOR_1);}};
		addWidget(availableMarketsWidget);
		int i = 0;
		for(String n : port.availableMarkets){
			availableMarketsWidget.addWidget(new AddableMarketEntryWidget(0,i*18,82,18,n));
			i++;
		}
		addWidget(new ImageWidget(0,24,105,112,BACKGROUND));
		addWidget(new LabelWidget(52,12,"label.markets_added").setXCentered(true));
		addedMarketsWidget = new DraggableScrollableWidgetGroup(3,28,102,106){{setYScrollBarWidth(3);setYBarStyle(null, TerminalTheme.COLOR_1);}};
		addWidget(addedMarketsWidget);
		i=0;
		for(String n:port.addedMarkets){
			addedMarketsWidget.addWidget(new RemovableMarketEntryWidget(0,i*18,99,18,n));
			i++;
		}


	}

	public void tryFixIcons(){
		for(Widget w : availableMarketsWidget.widgets){
			((AddableMarketEntryWidget)w).setIcon();
		}
		int i =0;
		for(Widget w : addedMarketsWidget.widgets){
			((RemovableMarketEntryWidget)w).setIcon(i);
			i++;
		}
	}

	@Override
    public boolean mouseReleased(int mouseX, int mouseY, int button) {
    	if(button ==0){	//really should be an enum or smth, left click
    		if(availableMarketsWidget.isMouseOverElement(mouseX,mouseY)){
				transferToAdded(mouseX,mouseY);
    		}
    		else if(addedMarketsWidget.isMouseOverElement(mouseX,mouseY)){
    			transferToAvailable(mouseX,mouseY);
    		}
    		addedMarketsWidget.setSize(addedMarketsWidget.getSize());
    		availableMarketsWidget.setSize(availableMarketsWidget.getSize());
    		return true;
    	}
    	return super.mouseReleased(mouseX,mouseY,button);
    }
    @Override
    public void drawInBackground(int mouseX, int mouseY, float partialTicks, IRenderContext context) {
    	if(!triedFix){
    		tryFixIcons();
    	}
    	super.drawInBackground(mouseX,mouseY,partialTicks,context);
    }

    private void transferToAvailable(int mouseX, int mouseY){
    	RemovableMarketEntryWidget entry = null;
    	int id=0;
    	for(Widget w : addedMarketsWidget.widgets){
    		id++;
			if(w.mouseClicked(mouseX, mouseY,0)){
				entry = (RemovableMarketEntryWidget)w;
				break;
			}
    	}
    	if(entry == null){return;}
		availableMarketsWidget.addWidget(entry.toAddable(0,availableMarketsWidget.widgets.size()*18));
		for(int i = addedMarketsWidget.widgets.size()-1;i>=id;i--){//count down from the end and decrement all the display circuits
			((RemovableMarketEntryWidget)addedMarketsWidget.widgets.get(i)).setCircuit(i-1);
			((RemovableMarketEntryWidget)addedMarketsWidget.widgets.get(i)).addSelfPosition(0,-18);
		}
		final int x = id-1;
		writeClientAction(1,buf ->{buf.writeBoolean(true);buf.writeInt(x);});
		port.availableMarkets.add(entry.market);
		port.addedMarkets.remove(entry.market);
		addedMarketsWidget.removeWidget(entry);
		port.markDirty();
    }
    private void transferToAdded(int mouseX, int mouseY){
    	AddableMarketEntryWidget entry = null;
    	int id=0;
    	for(Widget w : availableMarketsWidget.widgets){
    		id++;
			if(w.mouseClicked(mouseX, mouseY,0)){
				entry = (AddableMarketEntryWidget)w;
				break;
			}
    	}
    	if(entry == null){return;}
		addedMarketsWidget.addWidget(entry.toRemovable(0,addedMarketsWidget.widgets.size()*18,addedMarketsWidget.widgets.size()));
		for(int i = availableMarketsWidget.widgets.size()-1;i>=id;i--){//count down from the end and move them up
			availableMarketsWidget.widgets.get(i).addSelfPosition(0,-18);
		}
		final int x = id-1;
		writeClientAction(1,buf ->{buf.writeBoolean(false);buf.writeInt(x);});
		port.addedMarkets.add(entry.market);
		port.availableMarkets.remove(entry.market);
		availableMarketsWidget.removeWidget(entry);
		port.markDirty();
    }

	public void handleClientAction(int id, PacketBuffer buffer){
		if(id==1){
			boolean side = buffer.readBoolean();
			int index = buffer.readInt();
			if(side){ //to available
				port.availableMarkets.add(port.addedMarkets.get(index));
				port.addedMarkets.remove(index);
			}
			else{ //to added
				port.addedMarkets.add(port.availableMarkets.get(index));
				port.availableMarkets.remove(index);
			}
		}
	}





}
