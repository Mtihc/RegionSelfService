package com.mtihc.minecraft.regionselfservice.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.core1.ArgumentIterator;
import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.core1.exceptions.ArgumentFormatException;
import com.mtihc.minecraft.core1.exceptions.ArgumentIndexException;
import com.mtihc.minecraft.regionselfservice.Permissions;
import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;

public class WorthCommand extends BukkitCommand {

	public WorthCommand() {
		super("worth", "Find out how much a region is worth, specify region width and length. Or, an amount of money. Or, a region name.", "<width> <height> or <id> or <money>", null);
		List<String> help = new ArrayList<String>();
		help.add("Find out how much a region is worth.");
		help.add("Specify a region name. Or 2 numbers, the width and length.");
		help.add("Find out how big a region you can get.");
		help.add("Specify 1 number, the amount of money in your budget.");
		setLongDescription(help);
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
		
		if(!sender.hasPermission(Permissions.WORTH.toString())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission for that command.");
			return false;
		}
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		ArgumentIterator arguments = new ArgumentIterator(args);
		double blockWorth = plugin.config().settings().getBlockWorth();

		double arg1;
		try {
			// try to get argument 1 (width or money)
			arg1 = arguments.nextDouble();
		} catch (ArgumentIndexException e) {
			sender.sendMessage(ChatColor.RED
					+ "Incorrect number of arguments. Expected at least 1 number or a region name.");
			sender.sendMessage(getUsage());
			return false;
		} catch (ArgumentFormatException e) {
			// argument 1 is not a number, so it must be a region name
			String regionName;
			try {
				regionName = arguments.next();
			} catch (ArgumentIndexException exception) {
				//shouldn't happen here, exception would have already been caught
				sender.sendMessage(ChatColor.RED
						+ "Incorrect number of arguments. Expected a region name.");
				sender.sendMessage(getUsage());
				return false;
			}
			World world;
			String worldName;
			// second must be a region name, if not a player
			try {
				worldName = arguments.next();
				world = sender.getServer().getWorld(worldName);
				if(world == null) {
					sender.sendMessage(ChatColor.RED + "World '" + world + "' doesn't exist.");
					return false;
				}
			} catch(ArgumentIndexException exception) {
				// no world argument defined
				if(sender instanceof Player) {
					// take player's world
					world = ((Player)sender).getWorld();
					worldName = world.getName();
				}
				else {
					// if not player, send error message
					sender.sendMessage(ChatColor.RED + "Incorrect number or arguments. Expected world name.");
					sender.sendMessage(getUsage());
					return false;
				}
				
			}
			plugin.explainRegionWorth(sender, blockWorth, regionName, world);
			return true;
		}

		double arg2;
		try {
			// try to get argument 2 (height or undefined)
			arg2 = arguments.nextDouble();
		} catch (ArgumentIndexException e) {
			arg2 = -1;
		} catch (ArgumentFormatException e) {
			sender.sendMessage(ChatColor.RED
					+ "Invalid argument format. Expected a number.");
			sender.sendMessage(getUsage());
			return false;
		}

		
		// check existance of second argument
		if (arg2 == -1) {
			// show how big a region you can get for <arg1> money.
			plugin.explainRegionSize(sender, blockWorth, arg1);
		} else {
			int width = (int) arg1;
			int length = (int) arg2;
			// show how much money you need for a region of size <arg1> x <arg2>
			plugin.explainRegionWorth(sender, blockWorth, width, length);
		}
		
		return true;
	}

	
}
