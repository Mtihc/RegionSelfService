package com.mtihc.regionselfservice.v2.plugin;

import java.io.File;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.regionselfservice.v2.plots.IEconomy;
import com.mtihc.regionselfservice.v2.plots.IPlotManagerConfig;
import com.mtihc.regionselfservice.v2.plots.IPlotPermission;
import com.mtihc.regionselfservice.v2.plots.PlotManager;
import com.mtihc.regionselfservice.v2.plots.PlotWorld;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class SelfServiceManager extends PlotManager {

	
	public SelfServiceManager(JavaPlugin plugin, WorldGuardPlugin worldGuard,
			IEconomy economy, IPlotManagerConfig config, IPlotPermission perms) {
		
		super(
				plugin, 
				worldGuard, 
				economy, 
				config, 
				new PlotWorldConfig(
						plugin,
						new File(plugin.getDataFolder() + "/worlds"), 
						"world_default_config"), 
				perms);
		
	}

	@Override
	protected PlotWorld createPlotWorld(World world) {
		File configDir = new File(
				plugin.getDataFolder() + "/worlds");
		configDir.mkdirs();
		File plotDir = new File(
				plugin.getDataFolder() + "/system/regions");
		plotDir.mkdirs();
		
		PlotWorldConfig config = new PlotWorldConfig(
				plugin,
				configDir, 
				world.getName());
		
		config.getConfig().options().copyDefaults(true);
		config.getConfig().setDefaults(
				((PlotWorldConfig) defaultConfig).getConfig());
		
		PlotDataRepository plots = new PlotDataRepository(
				plotDir, 
				world.getName());
		
		return new PlotWorld(this, world, config, plots);
	}

}
