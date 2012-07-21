package com.mtihc.minecraft.regionselfservice.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.core.SimpleCommand;

public class DefineCommand extends SimpleCommand {

	private DefineExecutor executor;

	public DefineCommand(SimpleCommand parent, DefineExecutor executor, String permission) {
		super(parent, "define", null, permission, "You don't have permission for the define command.", "<id> [priority]", "Save the cuboid selection as protected region");
		List<String> aliases = new ArrayList<String>();
		aliases.add("set");
		aliases.add("create");
		this.setAliases(aliases);
		
		ArrayList<String> help = new ArrayList<String>();
		
		help.add("Protect the selected region.");
		help.add("Specify a unique id, and optionally a priority.");
		help.add("The priority will default to zero, if its undefined.");
		help.add("The new region will have a default bottom-Y and top-Y.");
		
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
		
		// get (optional) priority from arguments
		int priority;
		try {
			priority = Integer.parseInt(args[1]);
		} catch (IndexOutOfBoundsException e) {
			// priority is optional,
			// defaults to 0 if not defined
			priority = 0;
		} catch (NumberFormatException e) {
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

	@Override
	public boolean hasNested() {
		return true;
	}

	@Override
	public SimpleCommand getNested(String labelOrAlias) {
		String lbl = labelOrAlias.toLowerCase();
		if(lbl.equals("-exact") || lbl.equals("-ex")) {
			return new DefineExactCommand(this, executor, getPermission() + "-exact");
		}
		else {
			return null;
		}
	}

	@Override
	public String[] getNestedCommandLabels() {
		return new String[]{"-exact"};
	}

	
}
