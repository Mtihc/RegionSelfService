package com.mtihc.minecraft.regionselfservice.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.core1.ArgumentIterator;
import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.core1.exceptions.ArgumentIndexException;
import com.mtihc.minecraft.regionselfservice.Permissions;
import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;

public class RedefineCommand extends BukkitCommand {

	private DefineExecutor executor;

	public RedefineCommand(DefineExecutor executor) {
		super("redefine", "Change an existing region to your current selection", "<id>", null);
		List<String> aliases = new ArrayList<String>();
		aliases.add("reset");
		aliases.add("recreate");
		aliases.add("resize");
		this.setAliases(aliases);
		
		ArrayList<String> help = new ArrayList<String>();
		
		help.add("Change an existing region to your current selection.");
		help.add("The new region will have a default bottom-Y and top-Y.");
		
		setLongDescription(help);
		
		this.executor = executor;
	}


	/* (non-Javadoc)
	 * @see com.mtihc.bukkitplugins.core1.BukkitCommand#execute(org.bukkit.command.CommandSender, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(super.execute(sender, label, args))
		{
			return true;
		}
		
		if(!sender.hasPermission(Permissions.REDEFINE.toString())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission for that command.");
			return false;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command must be executed by a player, in game.");
			return false;
		}
		
		ArgumentIterator arguments = new ArgumentIterator(args);
	
		// get region name from arguments
		String regionName;
		try {
			regionName = arguments.next();
		} catch (ArgumentIndexException e) {
			// region name argument not defined
			sender.sendMessage(ChatColor.RED
					+ "Incorrect number of arguments. Expected region name.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		int topY = plugin.config().settings().getDefaultTopY();
		int bottomY = plugin.config().settings().getDefaultBottomY();
		return executor.redefine((Player)sender, regionName, topY, bottomY);
	}

}
