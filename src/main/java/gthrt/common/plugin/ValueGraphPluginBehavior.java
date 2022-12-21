package gthrt.common.plugin;

import gthrt.client.RenderUtil;
import gthrt.GTHRTMod;
import gthrt.common.market.Market;
import gthrt.common.market.MarketHandler;

import gregtech.api.items.behavior.MonitorPluginBaseBehavior;
import gregtech.common.gui.widget.monitor.WidgetPluginConfig;
import gregtech.api.gui.IUIHolder;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.gui.widgets.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.network.PacketBuffer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;


public class ValueGraphPluginBehavior extends MonitorPluginBaseBehavior{
	public static final List<Long> data = Arrays.asList(13L,44L,97L,88L,85L);
	public static final List<Long> data1 = Arrays.asList(62L, 54L, 44L, 85L, 25L);
	public boolean bottom;

	public void setConfig(boolean _bottom){
		this.bottom=_bottom;
		writePluginData(GregtechDataCodes.UPDATE_PLUGIN_CONFIG, packetBuffer -> {
			packetBuffer.writeBoolean(bottom);
		});
	}


    @Override
    public void readPluginData(int id, PacketBuffer buf) {
    	if(id == GregtechDataCodes.UPDATE_PLUGIN_CONFIG){
    		this.bottom = buf.readBoolean();
    	}
    }
    @Override
    public void writeToNBT(NBTTagCompound data) {
    	super.writeToNBT(data);
    	data.setBoolean("bottom",bottom);
    }
    @Override
    public void readFromNBT(NBTTagCompound data) {
    	super.readFromNBT(data);
    	this.bottom = data.hasKey("bottom") && data.getBoolean("bottom");
    }

	@Override
    public MonitorPluginBaseBehavior createPlugin() {
    	ValueGraphPluginBehavior out = new ValueGraphPluginBehavior();
    	out.bottom = true;
    	return out;
    }

	@SideOnly(Side.CLIENT)
    @Override
    public void renderPlugin(float partialTicks, RayTraceResult rayTraceResult) {
    	int i = 0;

    	for(String s : MarketHandler.markets.keySet()){
			Market m = MarketHandler.markets.get(s);
			RenderUtil.renderLineChart(m.valueHistory,
			10,-0.5f, 0.5f, 1, 1, bottom ? 0f : 0.01f, m.color,i);
			i++;
    	}
        /*RenderUtil.renderLineChart(data,100,-0.5f, 0.5f, 1, 1, bottom ? 0f : 0.01f, 0XFF03FF00,0);
        RenderUtil.renderLineChart(data1,100,-0.5f, 0.5f, 1, 1, bottom ? 0f : 0.01f, 0XFFFF0300,1f);*/
    }
	@Override
	public WidgetPluginConfig customUI(WidgetPluginConfig widgetGroup, IUIHolder holder, EntityPlayer entityPlayer) {
		return widgetGroup.setSize(200,200)
			.widget(new LabelWidget(40, 20, "bottom:", 0XFFFFFFFF))
			.widget(new ToggleButtonWidget(90, 10, 20, 20, ()->this.bottom, state -> setConfig(state)));
	}
}
