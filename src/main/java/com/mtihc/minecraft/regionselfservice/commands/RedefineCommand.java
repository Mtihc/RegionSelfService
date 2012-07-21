package com.mtihc.minecraft.regionselfservice.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.core.SimpleCommand;

public class RedefineCommand extends SimpleCommand {

	private DefineExecutor executor;

	public RedefineCommand(SimpleCommand parent, DefineExecutor executor, String permission) {
		super(parent, "redefine", null, permission, "You don't have permission to redefine regions.", "<id>", "Change an existing region to your current selection");
		List<String> aliases = new ArrayList<String>();
		aliases.add("reset");
		aliases.add("recreate");
		aliases.add("resize");
		this.setAliases(aliases);
		
		ArrayList<String> help = new ArrayList<String>();
		
		help.add("Change an existing region to your current selection.");
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
		
		
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		int topY = plugin.config().settings().getDefaultTopY();
		int bottomY = plugin.config().settings().getDefaultBottomY();
		return executor.redefine((Player)sender, regionName, topY, bottomY);
	}


	@Override
	public boolean hasNested() {
		return true;
	}


	@Override
	public SimpleCommand getNested(String labelOrAlias) {
		String lbl = labelOrAlias.toLowerCase();
		if(lbl.equals("-exact") || lbl.equals("-ex")) {
			return new RedefineExactCommand(this, executor, getPermission() + "-exact");
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
