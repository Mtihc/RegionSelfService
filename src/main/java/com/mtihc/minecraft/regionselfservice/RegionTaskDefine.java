package com.mtihc.minecraft.regionselfservice;

import java.util.Set;

import org.bukkit.World;

import com.mtihc.minecraft.regionselfservice.control.EconomyControl;
import com.mtihc.minecraft.regionselfservice.exceptions.PaymentException;
import com.mtihc.minecraft.regionselfservice.tasks.AcceptableTask;
import com.mtihc.minecraft.regionselfservice.tasks.AcceptableTaskException;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionTaskDefine extends AcceptableTask {

	private RegionSelfServicePlugin plugin;
	private Set<String> depositTo;
	private double cost;
	private ProtectedRegion region;
	private World world;
	private boolean bypassCost;

	public RegionTaskDefine(RegionSelfServicePlugin plugin, String playerName, ProtectedRegion region, World world) {
		this(plugin, playerName, null, 0, region, world, false);
	}
	
	public RegionTaskDefine(RegionSelfServicePlugin plugin, String withdrawFrom, Set<String> depositTo, double cost, ProtectedRegion region, World world, boolean bypassCost) {
		super(withdrawFrom);
		this.depositTo = depositTo;
		this.cost = cost;
		this.region = region;
		this.world = world;
		this.plugin = plugin;
		this.bypassCost = bypassCost;
	}

	@Override
	public void run(AcceptResult result) throws AcceptableTaskException {

		if(!(result.equals(AcceptResult.ACCEPTED) || result.equals(AcceptResult.NOT_REQUIRED))) {
			return;
		}
		
		EconomyControl economy = plugin.getEconomy();
		if(paymentRequired()) {
			if(!bypassCost) {
				try {
					if(cost > 0) {
						economy.withdraw(playerName, Math.abs(cost));
					}
					else {
						economy.deposit(playerName, Math.abs(cost));
					}
				} catch (PaymentException e) {
					throw new AcceptableTaskException(e.getMessage());
				}
				
			}
			
			
			if(depositTo != null && depositTo.size() != 0) {
				double share = Math.abs(cost) / depositTo.size();
				if(cost > 0) {
					for (String account : depositTo) {
						try {
							economy.deposit(account, share);
						} catch (PaymentException e) {
							// don't care,
							// as long as the player loses money
						}
					}
				}
				else {
					for (String account : depositTo) {
						try {
							economy.withdraw(account, share);
						} catch (PaymentException e) {
							// don't care,
							// as long as the player gets money
						}
					}
				}
				
			}
		}
		
		
		
		try {
			RegionManager mgr = plugin.getWorldGuard().getRegionManager(world);
			mgr.addRegion(region);
			mgr.save();
		} catch (ProtectionDatabaseException e) {
			throw new AcceptableTaskException("WorldGuard was unable to save region '" + region.getId() + "'. [WorldGuard] " + e.getMessage(), e);
		}
	}
	
	
	public boolean paymentRequired() {
		return cost != 0;
	}

	@Override
	public boolean acceptIsRequired() {
		return paymentRequired() && !bypassCost;
	}

	@Override
	public long getAcceptTime() {
		return 6000;
	}

	public double getCost() {
		return cost;
	}
	
	public ProtectedRegion getRegion() {
		return region;
	}

	public String withdrawFrom() {
		return playerName;
	}
	
	public Set<String> depositTo() {
		return depositTo;
	}
	
	public double depositShare() {
		int n = 0;
		if(depositTo != null) {
			n = depositTo.size();
		}
		return cost / n;
	}
	
	public World getWorld() {
		return world;
	}
	
}
