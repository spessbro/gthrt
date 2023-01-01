package gthrt.common.market;

import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Random;
import javax.annotation.Nonnull;

import gthrt.GTHRTMod;
import gthrt.common.HRTConfig;

public class MarketData extends WorldSavedData{
	public static final String DATA_NAME = GTHRTMod.MODID + "_MarketData";
	private static MarketData INSTANCE;
	public MarketData(String s){
		super(s);
	}

	@Override
	public @Nonnull
	NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
		NBTTagCompound out = new NBTTagCompound();

		for(Map.Entry<String,Market> e : MarketHandler.markets.entrySet()){
			out.setTag(e.getKey(),e.getValue().writeToNBT());
		}
		nbt.setTag("marketData",out);
		return nbt;
	}
	@Override
	public void readFromNBT(NBTTagCompound in){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			NBTTagCompound data = in.getCompoundTag("marketData");
			MarketHandler.markets.clear();
			for(Map.Entry<String,MarketBase> i : MarketHandler.marketTypes.entrySet()){
				NBTTagCompound tag = data.getCompoundTag(i.getKey());
				MarketHandler.markets.put(i.getKey(),
				!tag.getKeySet().isEmpty() ? Market.readFromNBT(tag,i.getValue()) : Market.fromBase(i.getValue()));
			}
		}
	}
    public static void setDirty() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && INSTANCE != null)
            INSTANCE.markDirty();
    }

    public static void setInstance(MarketData in) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
            INSTANCE = in;
    }

}
