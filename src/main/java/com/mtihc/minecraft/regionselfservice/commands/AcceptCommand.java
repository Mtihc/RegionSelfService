package com.mtihc.minecraft.regionselfservice.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.core.SimpleCommand;

public class AcceptCommand extends SimpleCommand {

	public AcceptCommand(SimpleCommand parent) {
		super(parent, "accept", null, null, "", "", "Accept an action. Usually payment.");
		ArrayList<String> aliases = new ArrayList<String>();
		aliases.add("yes");
		setAliases(aliases);
	}


	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		// expect no arguments
		if(args != null && args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Expected no arguments.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		RegionSelfServicePlugin.getPlugin().taskAccept(sender);
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
