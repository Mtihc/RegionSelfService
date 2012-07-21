package com.mtihc.minecraft.regionselfservice.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.regionselfservice.Permissions;
import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.RentSession;
import com.mtihc.minecraft.regionselfservice.core.SimpleCommand;
import com.mtihc.minecraft.regionselfservice.exceptions.PaymentException;
import com.mtihc.minecraft.regionselfservice.exceptions.WoodenSignException;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RentCommand extends SimpleCommand {

	public RentCommand(SimpleCommand parent, String permission) {
		super(parent, "rent", null, permission, "You don't have permission for the rent command.", "[hours]", null);
		
		List<String> help = new ArrayList<String>();
		help.add("Find a sign that says, for example: " + RegionSelfServicePlugin.getPlugin().config().settings().getFirstLineForRent().get(0));
		help.add("To rent the region that is related to the sign, ");
		help.add("look at it and execute this command.");
		help.add("Specify the amount of hours, or rent it for 1 hour by default");
		setLongDescription(help.toArray(new String[help.size()]));
		
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();

		// get player's target block
		Player player = (Player) sender;
		Block block = player.getTargetBlock(null, 5);
		World world = block.getWorld();
	
		// is target block a Fore Rent sign
		if(!plugin.woodenSigns().isSign(block)) {
			player.sendMessage(ChatColor.RED + "You are not looking at a sign.");
			return false;
		}

		Sign sign = (Sign) block.getState();
		List<String> firstLineOptions = plugin.config().settings().getFirstLineForRent();
		if (!plugin.woodenSigns().matchFirstLine(firstLineOptions, sign.getLine(0))) {
			player.sendMessage(ChatColor.RED + "You are not looking at a "
					+ ChatColor.WHITE + "For Rent" + ChatColor.RED + " sign.");
			return false;
		}

		int hoursTotal;
		try {
			hoursTotal = Integer.parseInt(args[0]);
		} catch (IndexOutOfBoundsException e) {
			hoursTotal = 1;
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED
					+ "Invalid hours format. Expected an integer number. ");
			return false;
		}
		
		int hoursMax = plugin.config().settings().getOnRentMaxHours();
		if(hoursTotal > hoursMax) {
			sender.sendMessage(ChatColor.RED + "You can rent this region for max " + ChatColor.WHITE + hoursMax + " hours");
			return false;
		}
	
		// get region cost on the sign
		double cost;
		try {
			cost = plugin.woodenSigns().getRegionCostOnSign(sign.getLines());
		} catch (WoodenSignException e) {
			player.sendMessage(ChatColor.RED + e.getMessage());
			return false;
		}
	
		// get region name on the sign
		String regionName;
		try {
			regionName = plugin.woodenSigns().getRegionNameOnSign(
					sign.getLines());
		} catch (WoodenSignException e) {
			player.sendMessage(ChatColor.RED + e.getMessage());
			return false;
		}
	
		// maybe bypass cost, but owners still get the money
		boolean bypassCost = sender.hasPermission(Permissions.RENT_BYPASSCOST);
	
		// get region
		RegionManager mgr = plugin.getWorldGuard().getRegionManager(world);
		ProtectedRegion region = mgr.getRegion(regionName);
		if (region == null) {
			player.sendMessage(ChatColor.RED + "Region '" + regionName
					+ "' doesn't exist in world '"
					+ player.getWorld().getName() + "'.");
		}
	
		LocalPlayer localPlayer = plugin.getWorldGuard().wrapPlayer(player);
		// is player already member
		if (region.isMember(localPlayer)) {
			player.sendMessage(ChatColor.RED
					+ "You are already member (or owner) of this region.");
			return false;
		}
	
		// pay for hours
		double costTotal = cost * hoursTotal;
		if(!bypassCost) {
			try {
				plugin.economy().withdraw(player.getName(), costTotal);
			} catch(PaymentException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return false;
			}
		}
		
		
		Set<String> owners = region.getOwners().getPlayers();
		Set<String> members = region.getMembers().getPlayers();
		
		double share = 0;
		int n = owners.size();
		if(n > 0) {
			share = cost / n;
		}
		
		
		for (String name : owners) {
			try {
				plugin.economy().deposit(name, share);
			} catch (PaymentException e) {
				
			}
		}
		
		region.getMembers().addPlayer(localPlayer);
		mgr.addRegion(region);
		try {
			mgr.save();
		} catch (ProtectionDatabaseException e) {
			sender.sendMessage(ChatColor.RED + "[WorldGuard] " + e.getMessage());
			return false;
		}
		
		// add/save/start/rentsession
		plugin.rental().startRentSession(new RentSession(plugin, player.getName(), player.getWorld().getName(), regionName, hoursTotal));
		
		plugin.messages().rented(player, owners, members, regionName, costTotal, hoursTotal + " hours");
		
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
