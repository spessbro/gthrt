package gthrt.command;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandBase;

import gthrt.GTHRTMod;
import gthrt.common.market.MarketHandler;

class CommandStep extends CommandBase{
	public String getName(){
		return "step";
	}
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		for(int i = 0;i<Integer.parseInt(args[0]);i++){
			MarketHandler.doStep();
		}
		GTHRTMod.logger.info("Stepped "+args[0]+" times");
	}
	public String getUsage(ICommandSender sender){
		return ":floppaxd:"; //todo
	}
	public int getRequiredPermissionLevel(){
		return 0;
	}
	public boolean checkPermission(MinecraftServer server,ICommandSender sender){
		return true;
	}
}
