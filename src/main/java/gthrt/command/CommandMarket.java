package gthrt.command;

import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
public class CommandMarket extends CommandTreeBase{
	public CommandMarket(){
		super.addSubcommand(new CommandStep());
	}
	public String getName(){
		return "market";
	}
	public String getUsage(ICommandSender sender){
		return "Lmao"; //todo
	}
	public int getRequiredPermissionLevel(){
		return 0;
	}
	public boolean checkPermission(MinecraftServer server,ICommandSender sender){
		return true;
	}
}
