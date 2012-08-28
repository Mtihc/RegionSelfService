package com.mtihc.regionselfservice.v2.plots;

import org.bukkit.World;
import org.bukkit.block.Sign;

import com.mtihc.regionselfservice.v2.plots.data.ISignData;
import com.mtihc.regionselfservice.v2.plots.data.PlotData;
import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class PlotWorld {

	protected final PlotManager manager;
	protected final World world;
	protected final IPlotWorldConfig config;
	protected final IPlotDataRepository plots;
	protected final RegionManager regionManager;

	public PlotWorld(PlotManager manager, World world, IPlotWorldConfig config, IPlotDataRepository plots) {
		this.manager = manager;
		this.world = world;
		this.config = config;
		this.plots = plots;
		
		this.regionManager = manager.getWorldGuard().getRegionManager(world);
	}

	public String getName() {
		return world.getName();
	}
	
	public World getWorld() {
		return world;
	}
	
	public IPlotWorldConfig getConfig() {
		return config;
	}
	
	public PlotManager getPlotManager() {
		return manager;
	}
	
	public IPlotDataRepository getPlotData() {
		return plots;
	}
	
	public Plot getPlot(String regionId) {
		PlotData data = plots.get(regionId);
		if(data == null) {
			return null;
		}
		return createPlot(data);
	}
	
	public Plot getPlot(Sign sign) throws SignException {
		ISignData data = manager.getSignValidator().createPlotSign(sign, sign.getLines());
		if(data == null) {
			return null;
		}
		return getPlot(data.getRegionId());
	}
	
	protected Plot createPlot(PlotData data) {
		return new Plot(this, data);
	}
	
	public RegionManager getRegionManager() {
		return regionManager;
	}

	
}
