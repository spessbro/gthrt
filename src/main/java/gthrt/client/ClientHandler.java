package gthrt.client;

import gthrt.common.market.MarketHandler;

import gregtech.api.unification.stack.ItemAndMetadata;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.Map;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientHandler{

    @SubscribeEvent
    public static void addValueTooltip(@Nonnull ItemTooltipEvent event){
    	Map.Entry<String,Float> entry = MarketHandler.sellableItems.get(new ItemAndMetadata(event.getItemStack()));
    	if(entry==null){return;}
		event.getToolTip().add(MarketHandler.makeTooltip(entry));
    }


}
