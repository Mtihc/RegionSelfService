package com.mtihc.minecraft.regionselfservice.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.core.SimpleCommand;

public class WorthCommand extends SimpleCommand {

	public WorthCommand(SimpleCommand parent, String permission) {
		super(parent, "worth", null, permission, "You don't have permission for the worth command.", "<width> <height> or <id> or <money>", "Find out how much a region is worth, specify region width and length. Or, an amount of money. Or, a region name.");
		List<String> help = new ArrayList<String>();
		help.add("Find out how much a region is worth.");
		help.add("Specify a region name. Or 2 numbers, the width and length.");
		help.add("Find out how big a region you can get.");
		help.add("Specify 1 number, the amount of money in your budget.");
		setLongDescription(help.toArray(new String[help.size()]));
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		double blockWorth = plugin.config().settings().getBlockWorth();

		double arg1;
		try {
			// try to get argument 1 (width or money)
			arg1 = Double.parseDouble(args[0]);
		} catch (IndexOutOfBoundsException e) {
			sender.sendMessage(ChatColor.RED
					+ "Incorrect number of arguments. Expected at least 1 number or a region name.");
			sender.sendMessage(getUsage());
			return false;
		} catch (NumberFormatException e) {
			// argument 1 is not a number, so it must be a region name
			String regionName = args[0];
			World world;
			String worldName;
			// second must be a region name, if not a player
			try {
				worldName = args[1];
				world = sender.getServer().getWorld(worldName);
				if(world == null) {
					sender.sendMessage(ChatColor.RED + "World '" + world + "' doesn't exist.");
					return false;
				}
			} catch(IndexOutOfBoundsException exception) {
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
			arg2 = Double.parseDouble(args[1]);
		} catch (IndexOutOfBoundsException e) {
			arg2 = -1;
		} catch (NumberFormatException e) {
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

	@Override
	public boolean hasNested() {
		return false;
	}

	@Override
	public SimpleCommand getNested(String labelOrAlias) {
		return null;
	}

	@Override
	public String[] getNestedCommandLabels() {
		return null;
	}

	
}
