package com.mtihc.minecraft.regionselfservice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.core.SimpleCommand;

public class ReloadCommand extends SimpleCommand {

	public ReloadCommand(SimpleCommand parent, String permission) {
		super(parent, "reload", null, permission, "You don't have permission to reload the config.",
				"", "Reloads the config.");
		
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if(args != null && args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Expected no arguments.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		plugin.config().settings().reload();
		plugin.config().signsRent().reload();
		plugin.config().signsSale().reload();
		
		sender.sendMessage(ChatColor.GREEN + "Configuration files reloaded.");
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
