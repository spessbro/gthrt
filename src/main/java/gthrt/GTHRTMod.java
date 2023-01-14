package gthrt;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import gregtech.api.GregTechAPI;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.common.items.MetaItems;
import static gregtech.api.unification.material.Materials.*;

import gthrt.common.HRTItems;
import gthrt.common.HRTUtils;
import gthrt.common.market.MarketData;
import gthrt.common.market.MarketPacket;
import gthrt.command.CommandMarket;
import gthrt.common.market.MarketHandler;

import gthrt.common.items.chains.PersonalHygieneChain;

/*import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import gregtechfoodoption.network.SPacketAppleCoreFoodDivisorUpdate;*/

@Mod(modid = GTHRTMod.MODID, name = GTHRTMod.NAME, version = GTHRTMod.VERSION,dependencies = "required-after:gregtech@(2.3.4,);"+"after:gregtechfoodoption")
public class GTHRTMod
{
    public static final String MODID = "gthrt";
    public static final String NAME = "GregTech Highly Randomized Trade";
    public static final String VERSION = "0.0";
    public static Logger logger;
	public static final boolean hasGTFO = Loader.isModLoaded("gregtechfoodoption");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
		HRTItems.preInit();
		MarketHandler.populateMarkets();
		GregTechAPI.networkHandler.registerPacket(MarketPacket.class);
    }

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
    	event.registerServerCommand(new CommandMarket());
	}
	@EventHandler
	public void serverStarted(FMLServerStartingEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			World world = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld();
			if(!world.isRemote){
				MarketData marketData = (MarketData) world.loadData(MarketData.class, MarketData.DATA_NAME);
				if(marketData==null){
					marketData = new MarketData(MarketData.DATA_NAME);
					world.setData(MarketData.DATA_NAME,marketData);
				}
				MarketData.setInstance(marketData);
				logger.info("Market Types available {}",MarketHandler.marketTypes.size());
				if(MarketHandler.markets.isEmpty()){
					marketData.readFromNBT(new NBTTagCompound());
				}
    			if(MarketHandler.markets.size()==0){logger.error("No Markets?");}
    			else{logger.info("Markets Loaded, markets size >> {}", MarketHandler.markets.size());}
			}
        }

    	logger.info("1 dynamite is worth {} credits",MarketHandler.getValue(MetaItems.DYNAMITE.getStackForm()));
  	}

    @EventHandler
    public void init(FMLInitializationEvent event){
        MarketHandler.handleItems();
        PersonalHygieneChain.registerRecipes();
    }
    /*@EventHandler
    public void conctruct(FMLConstructionEvent event){
    	new SPacketAppleCoreFoodDivisorUpdate();
    }
	@EventHandler
	public void onWorldLoad(WorldEvent.Load event){

	}*/

}
