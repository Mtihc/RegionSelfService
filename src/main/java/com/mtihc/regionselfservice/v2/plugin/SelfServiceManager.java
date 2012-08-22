package com.mtihc.regionselfservice.v2.plugin;

import java.io.File;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.regionselfservice.v2.plots.IEconomy;
import com.mtihc.regionselfservice.v2.plots.IPlotDataRepository;
import com.mtihc.regionselfservice.v2.plots.IPlotManagerConfig;
import com.mtihc.regionselfservice.v2.plots.IPlotPermission;
import com.mtihc.regionselfservice.v2.plots.IPlotWorldConfig;
import com.mtihc.regionselfservice.v2.plots.ISignValidator;
import com.mtihc.regionselfservice.v2.plots.PlotManager;
import com.mtihc.regionselfservice.v2.plots.PlotWorld;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class SelfServiceManager extends PlotManager {

	private File configDir;
	private File plotDir;
	
	public SelfServiceManager(JavaPlugin plugin, WorldGuardPlugin worldGuard,
			IEconomy economy, IPlotManagerConfig config,
			ISignValidator signValidator, IPlotPermission perms) {
		super(plugin, worldGuard, economy, config, signValidator, perms);
		configDir = new File(plugin.getDataFolder() + "/worlds");
		plotDir = new File(plugin.getDataFolder() + "/plots");
	}

	@Override
	protected PlotWorld createPlotWorld(World world) {
		IPlotWorldConfig config = new PlotWorldConfig(plugin, configDir, world.getName());
		IPlotDataRepository plots = new PlotDataRepository(plotDir, world.getName());
		return new PlotWorld(this, world, config, plots);
	}

}
