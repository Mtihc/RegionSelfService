package com.mtihc.regionselfservice.v2.plugin;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.regionselfservice.v2.plots.IPlotWorldConfig;
import com.mtihc.regionselfservice.v2.plugin.util.YamlFile;

public class PlotWorldConfig extends YamlFile implements IPlotWorldConfig {

	private String worldName;
	
	public PlotWorldConfig(JavaPlugin plugin, File dir, String worldName) {
		super(plugin, dir + "/" + worldName + ".yml");
		this.worldName = worldName;
	}
	
	@Override
	public String getWorldName() {
		return worldName;
	}

	@Override
	public double getBlockWorth() {
		return getConfig().getDouble("block-worth");
	}

	@Override
	public double getOnSellMinBlockCost() {
		return getConfig().getDouble("on-sell.min-block-cost");
	}

	@Override
	public double getOnSellMaxBlockCost() {
		return getConfig().getDouble("on-sell.max-block-cost");
	}

	@Override
	public double getOnRentMinBlockCost() {
		return getConfig().getDouble("on-rent.min-block-cost");
	}

	@Override
	public double getOnRentMaxBlockCost() {
		return getConfig().getDouble("on-rent.max-block-cost");
	}

}
