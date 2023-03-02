package gthrt.client;

import gthrt.common.market.MarketHandler;

import gregtech.api.unification.stack.ItemAndMetadata;
import gregtech.api.GTValues;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraftforge.client.event.ModelRegistryEvent;

import gthrt.common.block.BlockPackerCasing;
import gthrt.common.block.HRTBlocks;

import javax.annotation.Nonnull;
import java.util.Map;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientHandler{


	public static final IBlockColor PACKER_CASING_BLOCK_COLOR = ((state, worldIn, pos, tintIndex) -> GTValues.VC[HRTBlocks.PACKER_CASING.getState(state).ordinal()+1]);
	public static final IItemColor PACKER_CASING_ITEM_COLOR = ((state, tintIndex) -> GTValues.VC[HRTBlocks.PACKER_CASING.getState(state).ordinal()+1]);



  	@SubscribeEvent
  	public static void registerModels(ModelRegistryEvent event) {
		HRTBlocks.registerItemModels();
	}

    @SubscribeEvent
    public static void addValueTooltip(@Nonnull ItemTooltipEvent event){
    	Map.Entry<String,Float> entry = MarketHandler.sellableItems.get(new ItemAndMetadata(event.getItemStack()));
    	if(entry==null){return;}
		event.getToolTip().add(MarketHandler.makeTooltip(entry));
    }


}
