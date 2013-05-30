package com.mtihc.regionselfservice.v2.plots;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.regionselfservice.v2.plots.signs.ForRentSignData;
import com.mtihc.regionselfservice.v2.plots.signs.ForSaleSignData;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public abstract class PlotManager {

	static {
		ConfigurationSerialization.registerClass(PlotData.class);
		ConfigurationSerialization.registerClass(ForRentSignData.class);
		ConfigurationSerialization.registerClass(ForSaleSignData.class);
	}
	
	protected final JavaPlugin plugin;
	protected final WorldGuardPlugin worldGuard;
	private WorldEditPlugin worldEdit;
	protected final IEconomy economy;
	protected final IPlotManagerConfig config;
	protected final IPlotWorldConfig defaultConfig;
	protected final Map<String, PlotWorld> worlds;
	protected final IPlotPermission perms;
	protected final PlotControl control;
	
	

	public PlotManager(JavaPlugin plugin, WorldGuardPlugin worldGuard, IEconomy economy, IPlotManagerConfig config, IPlotWorldConfig defaultConfig, IPlotPermission perms) {
		this.plugin = plugin;
		this.worldGuard = worldGuard;
		try {
			this.worldEdit = worldGuard.getWorldEdit();
		} catch (CommandException e) {
			throw new IllegalArgumentException("Couldn't find WorldEdit.", e);
		}
		this.economy = economy;
		this.config = config;
		this.defaultConfig = defaultConfig;
		this.worlds = new HashMap<String, PlotWorld>();
		this.perms = perms;
		this.control = new PlotControl(this);
		
		Listener listener = new PlotListener(this);
		Bukkit.getPluginManager().registerEvents(listener, plugin);
		
	}
	
	public IPlotWorldConfig getDefaultWorldConfig() {
		return defaultConfig;
	}

	public void reloadWorld(World world) {
		PlotWorld plotWorld = createPlotWorld(world);
		this.worlds.put(plotWorld.getName(), plotWorld);
	}
	
	public void reloadWorlds() {
		List<World> worlds = Bukkit.getWorlds();
		for (World world : worlds) {
			reloadWorld(world);
		}
	}
	
	protected abstract PlotWorld createPlotWorld(World world);

	
	public JavaPlugin getPlugin() {
		return plugin;
	}

	public WorldGuardPlugin getWorldGuard() {
		return worldGuard;
	}
	
	public WorldEditPlugin getWorldEdit() {
		return worldEdit;
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
	
	public IPlotPermission getPermissions() {
		return perms;
	}
	
	public PlotControl getControl() {
		return control;
	}
}
