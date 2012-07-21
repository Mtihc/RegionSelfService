package com.mtihc.minecraft.regionselfservice.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.core.SimpleCommand;

public class CountCommand extends SimpleCommand {

	public CountCommand(SimpleCommand parent, String permission) {
		super(parent, "count", null, permission, "You don't have permission for the count command.", "[player] [world]", "Count how many regions you, or another player owns.");
	
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		String playerName;
		OfflinePlayer player;
		try {
			// defined player argument
			playerName = args[0];
			player = null;
		} catch (Exception e) {
			// didn't define player argument
			if (sender instanceof Player) {
				// sender is player,
				// player will be sender himself
				player = (Player) sender;
				playerName = player.getName();
			} else {
				// sender is not player, so no player found
				sender.sendMessage(ChatColor.RED
						+ "Incorrect number of arguments. Expected player name.");
				return false;
			}
		}
		
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		if (player == null) {
			// sender specified offline/online player
			player = sender.getServer().getOfflinePlayer(playerName);
			if (player != null) {
				playerName = player.getName();
			}
		}

		if (player == null) {
			// still no player found
			sender.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE
					+ "'" + playerName + "'" + ChatColor.RED
					+ " doesn't exist.");
			return false;
		}
		// we have a player

		World world;
		String worldName;
		try {
			worldName = args[1];
			world = null;
		} catch (Exception e) {
			if (sender instanceof Player) {
				// sender is player and didn't specify a world,
				// world will be sender's world
				world = ((Player) sender).getWorld();
				worldName = world.getName();
			} else {
				sender.sendMessage(ChatColor.RED
						+ "Incorrect number of arguments. Expected world name.");
				sender.sendMessage(getUsage());
				return false;
			}
		}

		if (world == null) {
			world = sender.getServer().getWorld(worldName);
			if (world == null) {
				sender.sendMessage(ChatColor.RED + "World " + ChatColor.WHITE
						+ "'" + worldName + "'" + ChatColor.RED
						+ " doesn't exist.");
				return false;
			}
		}

		// we have a world
		int count = plugin.regions().getRegionCountOfPlayer(world,
				playerName);

		String countString = "'" + String.valueOf(count) + "'";
		if (count < plugin.config().settings().getMaxRegionsPerPlayer()) {
			countString = ChatColor.WHITE + countString;
		} else {
			countString = ChatColor.RED + countString;
		}
		sender.sendMessage(ChatColor.GREEN + "Player " + ChatColor.WHITE + "'"
				+ player.getName() + "'" + ChatColor.GREEN + " has "
				+ countString + ChatColor.GREEN + " regions in world "
				+ ChatColor.WHITE + "'" + world.getName() + "'"
				+ ChatColor.GREEN + ".");
		
		
		
		
		
		
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
