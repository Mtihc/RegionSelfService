package com.mtihc.regionselfservice.v2.plots;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public abstract class PlotManager {

	protected final JavaPlugin plugin;
	protected final WorldGuardPlugin worldGuard;
	protected final IEconomy economy;
	protected final IPlotManagerConfig config;
	protected final IPlotWorldConfig defaultConfig;
	protected final Map<String, PlotWorld> worlds;
	protected final ISignValidator signValidator;
	protected final IPlotPermission perms;
	protected final PlotControl control;
	
	

	public PlotManager(JavaPlugin plugin, WorldGuardPlugin worldGuard, IEconomy economy, IPlotManagerConfig config, IPlotWorldConfig defaultConfig, ISignValidator signValidator, IPlotPermission perms) {
		this.plugin = plugin;
		this.worldGuard = worldGuard;
		this.economy = economy;
		this.config = config;
		this.defaultConfig = defaultConfig;
		this.worlds = new HashMap<String, PlotWorld>();
		this.signValidator = signValidator;
		this.perms = perms;
		this.control = new PlotControl(this);
		
		Listener listener = new PlotListener(this);
		Bukkit.getPluginManager().registerEvents(listener, plugin);
		
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
	
	public IEconomy getEconomy() {
		return economy;
	}
	
	public IPlotManagerConfig getConfig() {
		return config;
	}
	
	public PlotWorld getPlotWorld(String name) {
		return worlds.get(name);
	}
	
	public Collection<PlotWorld> getPlotWorlds() {
		return worlds.values();
	}
	
	public ISignValidator getSignValidator() {
		return signValidator;
	}
	
	public IPlotPermission getPermissions() {
		return perms;
	}
	
	public PlotControl getControl() {
		return control;
	}
}
