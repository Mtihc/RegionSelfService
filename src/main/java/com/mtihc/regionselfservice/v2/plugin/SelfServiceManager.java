package com.mtihc.regionselfservice.v2.plugin;

import java.io.File;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.regionselfservice.v2.plots.IEconomy;
import com.mtihc.regionselfservice.v2.plots.IPlotManagerConfig;
import com.mtihc.regionselfservice.v2.plots.PlotManager;
import com.mtihc.regionselfservice.v2.plots.PlotWorld;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;


public class SelfServiceManager extends PlotManager {
    
    public SelfServiceManager(JavaPlugin plugin, WorldGuardPlugin worldGuard, IEconomy economy, IPlotManagerConfig config) {
	super(plugin, worldGuard, economy, config, new PlotWorldConfigDefault(plugin, plugin.getDataFolder() + File.separator + "worlds" + File.separator + "world-config.yml", "world-config.yml"));
    }
    
    @Override
    protected PlotWorld createPlotWorld(World world) {
	
	File worldDir = new File(this.plugin.getDataFolder() + File.separator + "worlds" + File.separator + world.getName());
	
	PlotWorldConfig config = new PlotWorldConfig(new File(worldDir, "world-config.yml"), this.plugin.getLogger());
	config.reload();
	config.getConfig().options().copyHeader(false);
	config.getConfig().options().copyDefaults(false);
	config.getConfig().setDefaults(((PlotWorldConfigDefault) this.defaultConfig).getConfig());
	
	PlotDataRepository plots = new PlotDataRepository(worldDir + File.separator + "regions", this.plugin.getLogger()) {
	    
	    @Override
	    protected String getPathByKey(String regionId) {
		return this.directory + File.separator + regionId + ".yml";
	    }
	};
	return new PlotWorld(this, world, config, plots);
    }
    
}
