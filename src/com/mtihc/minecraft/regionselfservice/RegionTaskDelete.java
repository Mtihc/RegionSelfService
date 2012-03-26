package com.mtihc.minecraft.regionselfservice;

import java.util.Set;

import org.bukkit.World;

import com.mtihc.minecraft.regionselfservice.control.EconomyControl;
import com.mtihc.minecraft.regionselfservice.exceptions.PaymentException;
import com.mtihc.minecraft.regionselfservice.tasks.AcceptableTask;
import com.mtihc.minecraft.regionselfservice.tasks.AcceptableTaskException;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class RegionTaskDelete extends AcceptableTask {

	private Set<String> owners;
	private double worth;
	private RegionSelfServicePlugin plugin;
	private String region;
	private World world;
	private Set<String> members;
	
	public RegionTaskDelete(RegionSelfServicePlugin plugin, String playerName, String region, World world, Set<String> owners, Set<String> members, double worth) {
		super(playerName);
		this.plugin = plugin;
		this.region = region;
		this.world = world;
		this.owners = owners;
		this.members = members;
		this.worth = worth;
	}

	@Override
	public void run(AcceptResult result) throws AcceptableTaskException {
		if(!(result.equals(AcceptResult.ACCEPTED) || result.equals(AcceptResult.NOT_REQUIRED))) {
			return;
		}
		Set<String> owners = getOwners();
		int n = 0;
		if(owners != null && worth > 0) {
			n = owners.size();
			double share = worth / n;
			EconomyControl economy = plugin.getEconomy();
			for (String name : owners) {
				try {
					economy.deposit(name, share);
				} catch(PaymentException e) {
				}
			}
		}
		
		
		try {
			
			RegionManager mgr = plugin.getWorldGuard().getRegionManager(world);
			mgr.removeRegion(region);
			mgr.save();
			plugin.woodenSigns().breakAllRentSigns(region, world, true);
			plugin.woodenSigns().breakAllSaleSigns(region, world, true);
		
		} catch (ProtectionDatabaseException e) {
			throw new AcceptableTaskException("WorldGuard was unable to remove region '" + region + "'", e);
		}
	}
	
	public double getWorth() {
		return worth;
	}
	
	public Set<String> getOwners() {
		if(owners == null || owners.size() == 0) {
			return null;
		}
		else {
			return owners;
		}
	}
	
	public Set<String> getMembers() {
		if(members == null || members.size() == 0) {
			return null;
		}
		else {
			return members;
		}
	}

	@Override
	public boolean acceptIsRequired() {
		Set<String> owners = getOwners();
		if(owners != null) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public long getAcceptTime() {
		return 6000;
	}

	public String getRegionId() {
		return region;
	}

}
