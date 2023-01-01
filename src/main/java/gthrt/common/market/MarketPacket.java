package gthrt.common.market;


import gregtech.api.network.IPacket;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import gregtech.api.network.IClientExecutor;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

import gthrt.GTHRTMod;
import gthrt.common.HRTConfig;


public class MarketPacket implements IPacket, IClientExecutor {
	private Map<String,Market> markets;
	public MarketPacket(){}

	public MarketPacket(Map<String,Market> _markets){
		markets = _markets;
	}
	@Override
	public void encode(PacketBuffer buffer){
		NBTTagCompound tag = new NBTTagCompound();
		for(Map.Entry<String,Market> e : markets.entrySet()){
			tag.setTag(e.getKey(),e.getValue().writeToNBT());
		}
		GTHRTMod.logger.info("Encode >> {}" ,tag.toString());
		ByteBufUtils.writeTag(buffer,tag);

	}
	@Override
	public void decode(PacketBuffer buffer) {
		markets = new HashMap<>();
		NBTTagCompound compound = ByteBufUtils.readTag(buffer);
		for(Map.Entry<String,MarketBase> i : MarketHandler.marketTypes.entrySet()){
			Market m = Market.readFromNBT(compound.getCompoundTag(i.getKey()),i.getValue());
			markets.put(i.getKey(),m);
		}
	}
	@SideOnly(Side.CLIENT)
    @Override
    public void executeClient(NetHandlerPlayClient handler) {
        MarketHandler.markets = markets;
        GTHRTMod.logger.info("Synced markets >> {}",MarketHandler.marketTypes.size());
    }
}
