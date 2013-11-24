package com.mtihc.regionselfservice.v2.plots;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;


public interface IPlotSign extends IPlotSignData {

	public abstract Plot getPlot();
	
	public abstract PlotWorld getPlotWorld();
	
	public abstract Location getLocation();
	
	public abstract Sign getSign();
	
	public abstract Block getBlock();

}