package com.mtihc.minecraft.regionselfservice;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.regionselfservice.commands.AcceptCommand;
import com.mtihc.minecraft.regionselfservice.commands.BuyCommand;
import com.mtihc.minecraft.regionselfservice.commands.CountCommand;
import com.mtihc.minecraft.regionselfservice.commands.DefineCommand;
import com.mtihc.minecraft.regionselfservice.commands.DefineExactCommand;
import com.mtihc.minecraft.regionselfservice.commands.DefineExecutor;
import com.mtihc.minecraft.regionselfservice.commands.DeleteCommand;
import com.mtihc.minecraft.regionselfservice.commands.InfoCommand;
import com.mtihc.minecraft.regionselfservice.commands.RedefineCommand;
import com.mtihc.minecraft.regionselfservice.commands.RentCommand;
import com.mtihc.minecraft.regionselfservice.commands.WorthCommand;

public class RegionSelfServiceCommand extends BukkitCommand {

	
	public RegionSelfServiceCommand(RegionSelfServicePlugin plugin) {
		
		super("selfservice", "Reloads the plugin's configuration.", "", null);
		
		PluginCommand cmd = plugin.getCommand("selfservice");
		
		this.setAliases(cmd.getAliases());
		
		Server server = plugin.getServer();
		
		DefineExecutor defineExecutor = new DefineExecutor(plugin);
		
		List<String> help = new ArrayList<String>();
		help.add(this.getDescription());
		help.add(ChatColor.GREEN + "Nested commands:");
		help.add(this.getUsage());
		
		BukkitCommand buy = new BuyCommand();
		this.addNested(buy, server);
		help.add(buy.getUsage());
		
		BukkitCommand info = new InfoCommand();
		this.addNested(info, server);
		help.add(info.getUsage());
		
		BukkitCommand count = new CountCommand();
		this.addNested(count, server);
		help.add(count.getUsage());

		BukkitCommand worth = new WorthCommand();
		this.addNested(worth, server);
		help.add(worth.getUsage());
		
		BukkitCommand define = new DefineCommand(defineExecutor);
		BukkitCommand defineExact = new DefineExactCommand(defineExecutor);
		define.addNested(defineExact, server);
		this.addNested(define, server);
		help.add(define.getUsage());
		help.add(defineExact.getUsage());
		
		BukkitCommand redefine = new RedefineCommand(defineExecutor);
		BukkitCommand redefineExact = new DefineExactCommand(defineExecutor);
		redefine.addNested(redefineExact, server);
		this.addNested(redefine, server);
		help.add(redefine.getUsage());
		help.add(redefineExact.getUsage());
		
		BukkitCommand delete = new DeleteCommand();
		this.addNested(delete, server);
		help.add(delete.getUsage());
		
		BukkitCommand accept = new AcceptCommand();
		this.addNested(accept, server);
		help.add(accept.getUsage());
		
//		BukkitCommand reload = new ReloadCommand(control);
//		this.addNested(reload, server);
//		help.add(reload.getUsage());
		
		BukkitCommand rent = new RentCommand();
		this.addNested(rent, server);
		help.add(rent.getUsage());
		
		help.add(ChatColor.GREEN + "To get info about a command, just type " + ChatColor.WHITE + "?" + ChatColor.GREEN + " or " + ChatColor.WHITE + "help" + ChatColor.GREEN + " as first argument.");
		
		this.setLongDescription(help);
	}

	/* (non-Javadoc)
	 * @see com.mtihc.minecraft.core1.BukkitCommand#execute(org.bukkit.command.CommandSender, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(super.execute(sender, label, args)) {
			return true;
		}
		
		if(args.length != 0) {
			String cmd = args[0];
			sender.sendMessage(ChatColor.RED + "Unknown command '/" + label + " " + cmd + "'");
			sender.sendMessage(ChatColor.RED + "To get command help, type: " + ChatColor.WHITE + "/" + label + " " + "help");
			return true;
		}

		if(!sender.hasPermission(Permissions.RELOAD.toString())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to reload the configuration.");
			sender.sendMessage(ChatColor.RED + "To get command help, type: " + ChatColor.WHITE + "/" + label + " " + "help");
			return true;
		}
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		plugin.config().settings().reload();
		plugin.config().signsRent().reload();
		plugin.config().signsSale().reload();
		
		sender.sendMessage(ChatColor.GREEN + "Configuration files reloaded.");
		return true;
	}
	
	

}
