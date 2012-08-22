package com.mtihc.regionselfservice.v2.plugin;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.regionselfservice.v2.plots.IPlotManagerConfig;
import com.mtihc.regionselfservice.v2.plugin.util.YamlFile;

public class PlotManagerConfig extends YamlFile implements IPlotManagerConfig {

	public PlotManagerConfig(JavaPlugin plugin, String name) {
		super(plugin, name);
	}
	
	public List<String> getFirstLineForRent() {
		return getConfig().getStringList("sign-first-line.for-rent");
	}

	public List<String> getFirstLineForSale() {
		return getConfig().getStringList("sign-first-line.for-sale");
	}
}
