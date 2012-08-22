package com.mtihc.regionselfservice.v2.plots;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public abstract class PlotManager {

	private JavaPlugin plugin;
	private WorldGuardPlugin worldGuard;
	private Map<String, PlotWorld> worlds;

	public PlotManager(JavaPlugin plugin, WorldGuardPlugin worldGuard) {
		this.plugin = plugin;
		this.worldGuard = worldGuard;
		this.worlds = new HashMap<String, PlotWorld>();
		
		
		createPlotWorlds();
		
	}

	
	private void createPlotWorlds() {
		List<World> worlds = Bukkit.getWorlds();
		for (World world : worlds) {
			PlotWorld plotWorld = createPlotWorld(world);
			this.worlds.put(plotWorld.getName(), plotWorld);
		}
	}
	
	protected abstract PlotWorld createPlotWorld(World world);

	
	public JavaPlugin getPlugin() {
		return plugin;
	}

	public WorldGuardPlugin getWorldGuard() {
		return worldGuard;
	}
	
	public PlotWorld getPlotWorld(String name) {
		return worlds.get(name);
	}
	
	public Collection<PlotWorld> getPlotWorlds() {
		return worlds.values();
	}

}
