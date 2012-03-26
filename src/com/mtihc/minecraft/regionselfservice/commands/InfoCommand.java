package com.mtihc.minecraft.regionselfservice.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.core1.ArgumentIterator;
import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.core1.exceptions.ArgumentIndexException;
import com.mtihc.minecraft.regionselfservice.Permissions;
import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.exceptions.WoodenSignException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class InfoCommand extends BukkitCommand {

	public InfoCommand() {
		super("info", "Get info about a region. Look at a sign or specify the region name argument.", "[region] [world]", null);
		
		ArrayList<String> help = new ArrayList<String>();
		
		help.add("Get info about a region. Look at a sign ");
		help.add("while executing the command. ");
		help.add("Or specify a region id. And optionally a world name.");
		
		setLongDescription(help);
		
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
		
		
		if(!sender.hasPermission(Permissions.INFO)) {
			sender.sendMessage(ChatColor.RED + "You don't have permission for that command.");
			return false;
		}
		
		ArgumentIterator arguments = new ArgumentIterator(args);
		String regionName;
		try {
			// get region name from arguments
			regionName = arguments.next();
		} catch (ArgumentIndexException e) {
			// region name argument not defined
			if (!(sender instanceof Player)) {
				// region name is not optional for console
				sender.sendMessage(ChatColor.RED
						+ "Incorrect number of arguments. Expected region name.");
				sender.sendMessage(getUsage());
				// cannot execute
				return false;
			} else {
				// region name is optional for player!
				regionName = null;
			}
		}
		String worldName;
		World world;
		try {
			worldName = arguments.next();
		} catch (ArgumentIndexException e) {
			if (!(sender instanceof Player)) {
				// world name is not optional for console
				sender.sendMessage(ChatColor.RED
						+ "Incorrect number of arguments. Expected world name.");
				sender.sendMessage(getUsage());
				return false;
			} else {
				worldName = null;
			}
		}

		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		// did player specify a world name?
		// (for console it's not optional, see above)
		if (worldName == null) {
			world = ((Player) sender).getWorld();
			worldName = world.getName();
		} else {
			world = sender.getServer().getWorld(worldName);
			if (world == null) {
				sender.sendMessage(ChatColor.RED + "World " + ChatColor.WHITE
						+ worldName + ChatColor.RED + " does not exist.");
				return false;
			}
		}

		// did player specify a region name?
		// (for console it's not optional, see above)
		if (regionName == null) {
			// region name not defined
			// then this has to be a player
			Player player = (Player) sender;
			Block block = player.getTargetBlock(null, 3);
			if (!plugin.woodenSigns().isSign(block)) {
				// not looking at sign
				player.sendMessage(ChatColor.RED
						+ "You are not looking at a sign");
				return false;
			}

			Sign sign = (Sign) block.getState();

			// get region name on sign, when it's not defined as argument
			try {
				regionName = plugin.woodenSigns().getRegionNameOnSign(
						sign.getLines());
			} catch (WoodenSignException e) {
				// also no region on the sign
				player.sendMessage(ChatColor.RED + e.getMessage());
				return false;
			}

		}

		RegionManager mgr = plugin.getWorldGuard().getRegionManager(world);
		ProtectedRegion region = mgr.getRegion(regionName);
		if (region == null) {
			sender.sendMessage(ChatColor.RED + "Region " + ChatColor.WHITE
					+ "'" + regionName + "'" + ChatColor.RED
					+ " does not exist in world " + ChatColor.WHITE + "'"
					+ worldName + "'" + ChatColor.RED + ".");
			return false;
		}

		// we have passed all checks
		// console or player has specified a region
		double cost = plugin.config().signsSale().getRegionCost(worldName, regionName);
		if(cost == -1) {
			plugin.sendRegionInfo(sender, region, plugin.config().settings().getBlockWorth());
			sender.sendMessage(ChatColor.YELLOW + "Cost: " + ChatColor.WHITE + "Not for sale");
		}
		else {
			plugin.sendRegionInfo(sender, region, plugin.config().settings().getBlockWorth(), cost, false);
		}
		
		return true;
	}

}
