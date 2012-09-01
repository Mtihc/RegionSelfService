package com.mtihc.regionselfservice.v2.plots;

import org.bukkit.World;
import org.bukkit.block.Sign;

import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;
import com.mtihc.regionselfservice.v2.plots.signs.PlotSignType;
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
			data = new PlotData(world, regionId, 0, 0);
		}
		return createPlot(data);
	}
	
	public Plot getPlot(Sign sign) throws SignException {
		String regionId = PlotSignType.getRegionId(sign, sign.getLines());
		return getPlot(regionId);
	}
	
	protected Plot createPlot(PlotData data) {
		return new Plot(this, data);
	}
	
	public RegionManager getRegionManager() {
		return regionManager;
	}

	
}
