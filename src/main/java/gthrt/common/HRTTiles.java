package gthrt.common;

import gthrt.GTHRTMod;
import gthrt.common.port.WoodenDock;
import gthrt.common.port.SteelHarbor;
import gthrt.common.packingline.PackingLineController;

import net.minecraft.util.ResourceLocation;

import gregtech.api.GTValues;
import static gregtech.common.metatileentities.MetaTileEntities.*;

//using 11111-11511 because funy
public class HRTTiles{
	public static WoodenDock WOODEN_DOCK;
	public static SteelHarbor STEEL_HARBOR;
	public static PackingLineController[] PACKING_CONTROLLERS = new PackingLineController[8];;

	public static void init(){
		GTHRTMod.logger.info("Registering HRTEs");
		WOODEN_DOCK = registerMetaTileEntity(1111, new WoodenDock(new ResourceLocation(GTHRTMod.MODID,"wooden_dock")));
		STEEL_HARBOR = registerMetaTileEntity(1112, new SteelHarbor(new ResourceLocation(GTHRTMod.MODID,"steel_harbor")));
		for(int i = 0;i<PACKING_CONTROLLERS.length;i++){
			PACKING_CONTROLLERS[i] = registerMetaTileEntity(1113+i,
															new PackingLineController(new ResourceLocation(GTHRTMod.MODID,GTValues.VN[i+1].toLowerCase()+"_packing_controller"),i+1));
		}
	}
}
