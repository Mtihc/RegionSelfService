package com.mtihc.minecraft.regionselfservice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.core.SimpleCommand;

public class SellCommand extends SimpleCommand {

	public SellCommand(SimpleCommand parent, String permission) {
		super(parent, "sell", null, permission, "You don't have permission to sell regions.",
				"<price> <id>", "Create a region, and place a sell sign in one go.");
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Command must be executed by a player, in game.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		Player player = (Player) sender;
		
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();

		double price;
		try {
			price = Double.parseDouble(args[0]);
		} catch(Exception e) {
			sender.sendMessage(ChatColor.RED + "Expected region price and id.");
			sender.sendMessage(getUsage());
			return false;
		}
		String id;
		try {
			id = args[1];
		} catch(Exception e) {
			sender.sendMessage(ChatColor.RED + "Expected region price and id.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		
		DefineCommand cmd = new DefineCommand(getParent(), new DefineExecutor(plugin), getPermission());
		if(cmd.execute(sender, label, new String[]{id})) {
			
			
			String[] lines = new String[4];
			lines[0] = plugin.config().settings().getFirstLineForSale().get(0);
			lines[1] = String.valueOf(price);
			
			if(id.length() > 10) {
				lines[2] = id.substring(0, 10);
				lines[3] = id.substring(10, id.length());
			}
			else {
				lines[2] = id;
			}
			
			RegionSelfServicePlugin.addSignPlaceTask(player.getName(), lines);
			
			return true;
		}
		else {
			return false;
		}
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
