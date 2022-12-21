package gthrt.common.market;


import gregtech.api.network.IPacket;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import gregtech.api.network.IClientExecutor;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.nbt.NBTTagCompound;
import lombok.NoArgsConstructor;


import java.util.HashMap;
import java.util.Map;

import gthrt.GTHRTMod;
import gthrt.common.HRTConfig;

@NoArgsConstructor
public class MarketPacket implements IPacket, IClientExecutor {
	private Map<String,Market> markets;
	public MarketPacket(Map<String,Market> _markets){
		markets = _markets;
	}
	@Override
	public void encode(PacketBuffer buffer){
		NBTTagCompound tag = new NBTTagCompound();
		for(Map.Entry<String,Market> e : markets.entrySet()){
			tag.setTag(e.getKey(),e.getValue().writeToNBT());
		}
		ByteBufUtils.writeTag(buffer,tag);

	}
	@Override
	public void decode(PacketBuffer buffer) {
		markets.clear();
		NBTTagCompound compound = ByteBufUtils.readTag(buffer);
		for(String i : HRTConfig.MarketTypes){
			markets.put(i.substring(0, i.indexOf('|')),
						Market.readFromNBT(compound.getCompoundTag(i.substring(0, i.indexOf('|'))),i));
		}
	}
	@SideOnly(Side.CLIENT)
    @Override
    public void executeClient(NetHandlerPlayClient handler) {
    	if(MarketHandler.markets == null){GTHRTMod.logger.error("Ooops no markets on client"); return;}
        MarketHandler.markets = markets;
    }
}
