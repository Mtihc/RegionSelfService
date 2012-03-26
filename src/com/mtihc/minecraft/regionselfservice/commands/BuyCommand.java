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

import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.regionselfservice.Permissions;
import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;
import com.mtihc.minecraft.regionselfservice.exceptions.PaymentException;
import com.mtihc.minecraft.regionselfservice.exceptions.WoodenSignException;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class BuyCommand extends BukkitCommand {

	public BuyCommand() {
		super("buy", "Become owner of the region on the sign you're looking at.", "", null);
		
		List<String> help = new ArrayList<String>();
		String example = RegionSelfServicePlugin.getPlugin().config().settings().getFirstLineForSale().get(0);
		help.add("Find a sign that says, for example: " + example);
		help.add("To buy the region that is related to the sign, look at it and execute this command.");
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
		
		RegionSelfServicePlugin plugin = RegionSelfServicePlugin.getPlugin();
		
		// has permission and is player
		if (!sender.hasPermission(Permissions.BUY.toString())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to execute that command.");
			return false;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command should be executed by a player, in game.");
			return false;
		}
	
		// get player's target block
		Player player = (Player) sender;
		Block block = player.getTargetBlock(null, 5);
	
		// is target block a sign
		if(!isSign(block)) {
			player.sendMessage(ChatColor.RED + "You are not looking at a sign.");
			return false;
		}
		// typecast to Sign state
		Sign sign = (Sign) block.getState();
		
		// get first line options, for a For-Sale sign
		List<String> firstLineOptions = plugin.config().settings().getFirstLineForSale();
		
		if (!firstLineMatch(firstLineOptions, sign.getLine(0))) {
			player.sendMessage(ChatColor.RED + "You are not looking at a "
					+ ChatColor.WHITE + "For Sale" + ChatColor.RED + " sign.");
			return false;
		}
		World world = block.getWorld();
		int regionCount = plugin.regions().getRegionCountOfPlayer(
				world, player.getName());
		
		
		// has player reached max regions, or bypass
		boolean bypassMax = sender.hasPermission(Permissions.BYPASSMAX_REGIONS);
		if (!bypassMax) {
			int regionMax = plugin.config().settings().getMaxRegionsPerPlayer();
			
			if (regionCount >= regionMax) {
				player.sendMessage(ChatColor.RED + "You already have "
						+ ChatColor.WHITE + regionCount + ChatColor.RED
						+ " regions");
				return false;
			}
		}
	
		// get region cost on the sign
		double cost;
		try {
			cost = plugin.woodenSigns().getRegionCostOnSign(sign.getLines());
		} catch (WoodenSignException e) {
			player.sendMessage(ChatColor.RED + e.getMessage());
			return false;
		}
		boolean reserve = plugin.config().settings().getOnBuyReserveFreeRegions();
		
		if(reserve && cost == 0 && regionCount > 0) {
			sender.sendMessage(ChatColor.RED + "Free regions are reserved for new players!");
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
	
		
		// get region manager
		RegionManager mgr = plugin.getWorldGuard().getRegionManager(world);
		
		// get region
		ProtectedRegion region = mgr.getRegion(regionName);
		if (region == null) {
			player.sendMessage(ChatColor.RED + "Region '" + regionName
					+ "' doesn't exist in world '"
					+ player.getWorld().getName() + "'.");
		}
	
		LocalPlayer localPlayer = plugin.getWorldGuard().wrapPlayer(player);
		// is player already owner
		if (region.isOwner(localPlayer)) {
			player.sendMessage(ChatColor.RED
					+ "You are already owner of this region.");
			return false;
		}
		

		Set<String> owners = region.getOwners().getPlayers();
		Set<String> members = region.getMembers().getPlayers();
		
		if(plugin.config().settings().getOnBuyReserveFreeRegions()) {
			// check if players would become homless after sale
			// this is part of preventing cheating with free regions
			
			if(owners.size() != 0) {
				String homeless = "";
				for (String owner : owners) {
					int ownerRegionCount = plugin.regions().getRegionCountOfPlayer(world, owner);
					if(ownerRegionCount - 1 == 0) {
						homeless += ", " + owner;
					}
					
				}
				if(!homeless.isEmpty()) {
					homeless = homeless.substring(2);
					player.sendMessage(ChatColor.RED + "Sorry, you can't buy this region. The following players would become homeless: " + homeless);
					return false;
				}
			}
			
			
		}
		
		// maybe bypass cost, but owners still get the money
		boolean bypassCost = sender.hasPermission(Permissions.BUY_BYPASSCOST);
		
		// pay for region
		if(!bypassCost) {
			try {
				plugin.economy().withdraw(player.getName(), cost);
			} catch(PaymentException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return false;
			}
		}
		
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
		
		DefaultDomain newOwners = new DefaultDomain();
		newOwners.addPlayer(localPlayer);
		region.setOwners(newOwners);
		
		mgr.addRegion(region);
		try {
			mgr.save();
		} catch (ProtectionDatabaseException e) {
			sender.sendMessage(ChatColor.RED + "[WorldGuard] " + e.getMessage());
			return false;
		}
		
		plugin.woodenSigns().breakAllSaleSigns(regionName, player.getWorld(), true);
		plugin.config().signsSale().clearRegion(player.getWorld().getName(), regionName);
		
		plugin.messages().bought(player, owners, members, regionName, cost);
		
		return true;
	}

	private boolean isSign(Block block) {
		return (block != null && block.getState() instanceof Sign);
	}
	
	private boolean firstLineMatch(List<String> options, String line) {
		for (String option : options) {
			if(line.equalsIgnoreCase(option)) {
				return true;
			}
		}
		return false;
	}
}
