package gthrt.common;

import gthrt.GTHRTMod;

import net.minecraftforge.common.config.Config;

@Config(modid=GTHRTMod.MODID)
public class HRTConfig{

	@Config.Comment("Ticks per market step; 1200 ticks = 1 minute; default=6000")
	public static int ticksPerStep = 6000;

}
