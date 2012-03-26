package com.mtihc.minecraft.regionselfservice.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;

public class AcceptCommand extends BukkitCommand {

	public AcceptCommand() {
		super("accept", "Accept an action. Usually payment.", "", null);
		ArrayList<String> aliases = new ArrayList<String>();
		aliases.add("yes");
		setAliases(aliases);
	}

	/* (non-Javadoc)
	 * @see com.mtihc.minecraft.core1.BukkitCommand#execute(org.bukkit.command.CommandSender, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(super.execute(sender, label, args)) {
			return true;
		}
		
		// expect no arguments
		if(args != null && args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Expected no arguments");
			return false;
		}
		
		RegionSelfServicePlugin.getPlugin().taskAccept(sender);
		
		return true;
	}
	
	

}
