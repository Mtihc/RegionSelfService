package com.mtihc.regionselfservice.v2.plugin;

import java.io.File;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.regionselfservice.v2.plots.IPlotWorldConfig;
import com.mtihc.regionselfservice.v2.plugin.util.YamlFile;

public class PlotWorldConfig extends YamlFile implements IPlotWorldConfig {

	private String worldName;
	
	public PlotWorldConfig(JavaPlugin plugin, File dir, String worldName) {
		super(plugin, dir + File.separator + worldName + ".yml");
		this.worldName = worldName;
		reload();
	}
	
	@Override
	public String getWorldName() {
		return worldName;
	}

	@Override
	public double getBlockWorth() {
		return getConfig().getDouble("block_worth");
	}

	@Override
	public double getOnSellMinBlockCost() {
		return getConfig().getDouble("sell_min_block_cost");
	}

	@Override
	public double getOnSellMaxBlockCost() {
		return getConfig().getDouble("sell_max_block_cost");
	}

	@Override
	public double getOnRentMinBlockCost() {
		return getConfig().getDouble("rent_min_block_cost");
	}

	@Override
	public double getOnRentMaxBlockCost() {
		return getConfig().getDouble("rent_max_block_cost");
	}

	@Override
	public int getMaxRegionCount() {
		return getConfig().getInt("max_regions_per_player");
	}

	@Override
	public boolean isReserveFreeRegionsEnabled() {
		return getConfig().getBoolean("reserve_free_regions");
	}

	@Override
	public int getMinimumY() {
		return getConfig().getInt("region_size.minimum_y");
	}

	@Override
	public int getMaximumY() {
		return getConfig().getInt("region_size.maximum_y");
	}

	@Override
	public int getMinimumHeight() {
		return getConfig().getInt("region_size.minimum_height");
	}

	@Override
	public int getMaximumHeight() {
		return getConfig().getInt("region_size.maximum_height");
	}

	@Override
	public int getMinimumWidthLength() {
		return getConfig().getInt("region_size.minimum_width_length");
	}

	@Override
	public int getMaximumWidthLength() {
		return getConfig().getInt("region_size.maximum_width_length");
	}

	@Override
	public int getDefaultBottomY() {
		return getConfig().getInt("region_defaults.bottom_y");
	}

	@Override
	public int getDefaultTopY() {
		return getConfig().getInt("region_defaults.top_y");
	}

	@Override
	public List<String> getDefaultOwners() {
		return getConfig().getStringList("region_defaults.owners");
	}

	@Override
	public boolean isOverlapUnownedRegionAllowed() {
		return getConfig().getBoolean("allow_overlap_unowned_regions");
	}

	@Override
	public boolean isAutomaticParentEnabled() {
		return getConfig().getBoolean("region_defaults.parent_automatic");
	}

	@Override
	public boolean isCreateCostEnabled() {
		return getConfig().getBoolean("enable_create_cost");
	}

}
