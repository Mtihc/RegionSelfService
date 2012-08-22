package com.mtihc.regionselfservice.v2.plots;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

import com.mtihc.regionselfservice.v2.plots.data.ISignData;

public interface ISign extends ISignData {

	public abstract Plot getPlot();
	public abstract World getWorld();
	public abstract Location getLocation();
	public abstract Sign getSign();
}
