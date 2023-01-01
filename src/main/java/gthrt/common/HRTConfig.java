package gthrt.common;

import gthrt.GTHRTMod;

import net.minecraftforge.common.config.Config;

@Config(modid=GTHRTMod.MODID)
public class HRTConfig{
	@Config.Comment({"Defined Markets formatted like so", "name|baseValue|scale|volatility|elasticity|color"})
	@Config.RequiresMcRestart
	public static String[] MarketTypes={"PersonalHygiene|1|1000|0.5|0.5|1466CD"};

	@Config.Comment("Ticks per market step; 1200 ticks = 1 minute; default=6000")
	public static int ticksPerStep = 6000;

}
