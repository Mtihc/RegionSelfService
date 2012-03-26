package com.mtihc.minecraft.regionselfservice.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.core1.ArgumentIterator;
import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.core1.exceptions.ArgumentFormatException;
import com.mtihc.minecraft.core1.exceptions.ArgumentIndexException;
import com.mtihc.minecraft.regionselfservice.Permissions;
import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;

public class DefineCommand extends BukkitCommand {

	private DefineExecutor executor;

	public DefineCommand(DefineExecutor executor) {
		super("define", "Save the cuboid selection as protected region", "<id> [priority]", null);
		List<String> aliases = new ArrayList<String>();
		aliases.add("set");
		aliases.add("create");
		this.setAliases(aliases);
		
		ArrayList<String> help = new ArrayList<String>();
		
		help.add("Protect the selected region.");
		help.add("Specify a unique id, and optionally a priority.");
		help.add("The priority will default to zero, if its undefined.");
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
		
		if(!sender.hasPermission(Permissions.CREATE.toString())) {
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
		
		// get (optional) priority from arguments
		int priority;
		try {
			priority = arguments.nextInt();
		} catch (ArgumentIndexException e) {
			// priority is optional,
			// defaults to 0 if not defined
			priority = 0;
		} catch (ArgumentFormatException e) {
			// didn't type a number
			sender.sendMessage(ChatColor.RED
					+ "Invalid argument format. Expected integer number for priority.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		int topY = RegionSelfServicePlugin.getPlugin().config().settings().getDefaultTopY();
		int bottomY = RegionSelfServicePlugin.getPlugin().config().settings().getDefaultBottomY();
		return executor.define((Player)sender, regionName, priority, topY, bottomY);
	}

	
}
