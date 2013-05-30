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
				new PlotWorldConfigDefault(
						plugin,
						plugin.getDataFolder() + "\\worlds\\world-config.yml", 
						"world-config.yml"), 
				perms);
		
	}

	@Override
	protected PlotWorld createPlotWorld(World world) {
		
		File worldDir = new File(plugin.getDataFolder() + "\\worlds\\" + world.getName());
		
		PlotWorldConfig config = new PlotWorldConfig(new File(worldDir, "world-config.yml"), plugin.getLogger());
		config.reload();
		config.getConfig().options().copyHeader(false);
		config.getConfig().options().copyDefaults(false);
		config.getConfig().setDefaults(
				((PlotWorldConfigDefault) defaultConfig).getConfig());

		PlotDataRepository plots = new PlotDataRepository(worldDir, plugin.getLogger()) {

			@Override
			protected String getPathByKey(String regionId) {
				return directory + "\\regions\\" + regionId + ".yml";
			}
			
		};
		
		
		return new PlotWorld(this, world, config, plots);
	}

}
