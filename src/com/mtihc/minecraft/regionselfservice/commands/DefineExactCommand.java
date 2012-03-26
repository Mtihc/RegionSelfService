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

public class DefineExactCommand extends BukkitCommand {

	private DefineExecutor executor;

	public DefineExactCommand(DefineExecutor executor) {
		super("-exact", "Save the cuboid selection as protected region, overrides default top-y and bottom-y.", "<id> [top-y] [bottom-y]", null);
		List<String> aliases = new ArrayList<String>();
		aliases.add("-ex");
		this.setAliases(aliases);
		
		ArrayList<String> help = new ArrayList<String>();
		
		help.add("Protect the selected region.");
		help.add("Specify a unique id, and optionally ");
		help.add("the bottom-Y and top-Y coordinate.");
		help.add("If you don't define the y-coordinates, ");
		help.add("the region will be your exact selection.");
		
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
		
		if(!sender.hasPermission(Permissions.CREATE_EXACT.toString())) {
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
		int topY;
		try {
			topY = arguments.nextInt();
		} catch (ArgumentIndexException e) {
			// topY is optional,
			// defaults to selection's top y if not defined
			topY = -1;
		} catch (ArgumentFormatException e) {
			// didn't type a number
			sender.sendMessage(ChatColor.RED
					+ "Invalid argument format. Expected integer number for top-y.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		int bottomY;
		try {
			bottomY = arguments.nextInt();
		} catch (ArgumentIndexException e) {
			// bottomY is optional,
			// defaults to selection's bottom y if not defined
			bottomY = -1;
		} catch (ArgumentFormatException e) {
			// didn't type a number
			sender.sendMessage(ChatColor.RED
					+ "Invalid argument format. Expected integer number for bottom-y.");
			sender.sendMessage(getUsage());
			return false;
		}

		return executor.define((Player)sender, regionName, 0, topY, bottomY);
	}
}
