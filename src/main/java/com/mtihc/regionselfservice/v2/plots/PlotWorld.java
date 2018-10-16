package com.mtihc.regionselfservice.v2.plots;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Sign;

import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;
import com.mtihc.regionselfservice.v2.plots.signs.PlotSignText;
import com.sk89q.worldguard.protection.managers.RegionManager;


public class PlotWorld {
    
    protected final PlotManager manager;
    protected final String worldName;
    protected final IPlotWorldConfig config;
    protected final IPlotDataRepository plots;
    protected final RegionManager regionManager;
    
    public PlotWorld(PlotManager manager, World world, IPlotWorldConfig config, IPlotDataRepository plots) {
	this.manager = manager;
	this.worldName = world.getName();
	this.config = config;
	this.plots = plots;
	
	this.regionManager = manager.getWorldGuard().getRegionManager(world);
    }
    
    public String getName() {
	return this.worldName;
    }
    
    public World getWorld() {
	return Bukkit.getWorld(this.worldName);
    }
    
    public IPlotWorldConfig getConfig() {
	return this.config;
    }
    
    public PlotManager getPlotManager() {
	return this.manager;
    }
    
    public IPlotDataRepository getPlotData() {
	return this.plots;
    }
    
    public Plot getPlot(String regionId) {
	PlotData data = this.plots.get(regionId);
	if (data == null) {
	    data = new PlotData(regionId, 0, 0, 0);
	}
	return createPlot(data);
    }
    
    public Plot getPlot(Sign sign) throws SignException {
	String regionId = PlotSignText.getRegionId(sign.getLines());
	return getPlot(regionId);
    }
    
    protected Plot createPlot(PlotData data) {
	return new Plot(this, data);
    }
    
    public RegionManager getRegionManager() {
	return this.regionManager;
    }
    
    public Set<String> getPotentialHomeless(Set<String> names) {
	Set<String> result = new HashSet<String>();
	World world = getWorld();
	for (String name : names) {
	    int count = this.manager.control.getRegionCountOfPlayer(world, name);
	    if (count - 1 <= 0) {
		result.add(name);
	    }
	}
	return result;
    }
    
}
