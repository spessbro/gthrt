package gthrt.common;

import gthrt.GTHRTMod;
import gthrt.common.port.WoodenDock;

import net.minecraft.util.ResourceLocation;

import static gregtech.common.metatileentities.MetaTileEntities.*;

//using 11111-11511 because funy
public class HRTTiles{
	public static WoodenDock WOODEN_DOCK;

	public static void init(){
		GTHRTMod.logger.info("Registering TEs");
		WOODEN_DOCK = registerMetaTileEntity(1111, new WoodenDock(new ResourceLocation(GTHRTMod.MODID,"wooden_dock")));
	}
}
