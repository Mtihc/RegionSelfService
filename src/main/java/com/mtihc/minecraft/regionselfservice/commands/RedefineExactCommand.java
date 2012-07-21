package com.mtihc.minecraft.regionselfservice.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.regionselfservice.core.SimpleCommand;

public class RedefineExactCommand extends SimpleCommand {

	private DefineExecutor executor;

	public RedefineExactCommand(SimpleCommand parent, DefineExecutor executor, String permission) {
		super(parent, "-exact", null, permission, "You don't have permission to redefine regions exact.", "<id> [top-y] [bottom-y]", "Save the cuboid selection as protected region, overrides default top-y and bottom-y.");
		List<String> aliases = new ArrayList<String>();
		aliases.add("-ex");
		this.setAliases(aliases);
		
		ArrayList<String> help = new ArrayList<String>();
		
		help.add("Protect the selected region.");
		help.add("Specify a unique id, and optionally ");
		help.add("the bottom-Y and top-Y coordinate.");
		help.add("If you don't define the y-coordinates, ");
		help.add("the region will be your exact selection.");
		
		setLongDescription(help.toArray(new String[help.size()]));
		
		this.executor = executor;
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command must be executed by a player, in game.");
			return false;
		}
		
		// get region name from arguments
		String regionName;
		try {
			regionName = args[0];
		} catch (Exception e) {
			// region name argument not defined
			sender.sendMessage(ChatColor.RED
					+ "Incorrect number of arguments. Expected region name.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		
		
		int topY;
		int bottomY;
		try {
			topY = Integer.parseInt(args[1]);
			bottomY = Integer.parseInt(args[2]);
		} catch (IndexOutOfBoundsException e) {
			// topY and bottomY are optional,
			// defaults to selection
			topY = -1;
			bottomY = -1;
		} catch (NumberFormatException e) {
			// didn't type a number
			sender.sendMessage(ChatColor.RED
					+ "Invalid argument format. Expected integer number for top-y.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		return executor.redefine((Player)sender, regionName, topY, bottomY);
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
