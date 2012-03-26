package com.mtihc.minecraft.regionselfservice.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.core1.ArgumentIterator;
import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.core1.exceptions.ArgumentIndexException;
import com.mtihc.minecraft.regionselfservice.Permissions;
import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;

public class CountCommand extends BukkitCommand {

	public CountCommand() {
		super("count", "Find out how many regions you, or other players own.", "[player] [world]", null);
	
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
		
		
		if(!sender.hasPermission(Permissions.COUNT.toString())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission for that command.");
			return false;
		}
		
		ArgumentIterator arguments = new ArgumentIterator(args);
		
		
		String playerName;
		OfflinePlayer player;
		try {
			// defined player argument
			playerName = arguments.next();
			player = null;
		} catch (ArgumentIndexException e) {
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
			worldName = arguments.next();
			world = null;
		} catch (ArgumentIndexException e) {
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

}
