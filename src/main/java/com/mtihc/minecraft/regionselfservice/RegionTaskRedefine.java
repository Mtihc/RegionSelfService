package com.mtihc.minecraft.regionselfservice;

import java.util.Set;

import org.bukkit.World;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionTaskRedefine extends RegionTaskDefine {

	private ProtectedRegion existing;

	public RegionTaskRedefine(RegionSelfServicePlugin plugin,
			String playerName, ProtectedRegion region, World world) {
		super(plugin, playerName, region, world);
	}

	public RegionTaskRedefine(RegionSelfServicePlugin plugin,
			String withdrawFrom, Set<String> depositTo, double cost,
			ProtectedRegion region, World world, boolean bypassCost) {
		super(plugin, withdrawFrom, depositTo, cost, region, world, bypassCost);
		WorldGuardPlugin worldGuard = plugin.getWorldGuard();
		RegionManager mgr = worldGuard.getRegionManager(world);
		this.existing = mgr.getRegion(region.getId());
	}

	public ProtectedRegion getExisting() {
		return existing;
	}
}
