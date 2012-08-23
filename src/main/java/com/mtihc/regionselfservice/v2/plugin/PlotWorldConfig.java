package com.mtihc.regionselfservice.v2.plugin;

import java.io.File;
import java.util.List;

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

	@Override
	public int getMaxRegionCount() {
		return getConfig().getInt("max-regions-per-player", 1);
	}

	@Override
	public boolean isReserveFreeRegionsEnabled() {
		return getConfig().getBoolean("reserve-free-regions", true);
	}

	@Override
	public int getMinimumY() {
		return getConfig().getInt("on-create.sizes.minimum-y", 1);
	}

	@Override
	public int getMaximumY() {
		return getConfig().getInt("on-create.sizes.maximum-y", 255);
	}

	@Override
	public int getMinimumHeight() {
		return getConfig().getInt("on-create.sizes.minimum-height", 8);
	}

	@Override
	public int getMaximumHeight() {
		return getConfig().getInt("on-create.sizes.maximum-height", 128);
	}

	@Override
	public int getMinimumWidthLength() {
		return getConfig().getInt("on-create.sizes.minimum-width-length", 8);
	}

	@Override
	public int getMaximumWidthLength() {
		return getConfig().getInt("on-create.sizes.maximum-width-length", 128);
	}

	@Override
	public int getDefaultBottomY() {
		return getConfig().getInt("on-create.default-bottom-y", 0);
	}

	@Override
	public int getDefaultTopY() {
		return getConfig().getInt("on-create.default-top-y", 128);
	}

	@Override
	public List<String> getDefaultOwners() {
		return getConfig().getStringList("on-create.default-owners");
	}

	@Override
	public boolean isOverlapUnownedRegionAllowed() {
		return getConfig().getBoolean("on-create.allow-overlap-unowned-regions", false);
	}

	@Override
	public boolean isAutomaticParentEnabled() {
		return getConfig().getBoolean("on-create.automatic-parent", true);
	}

	@Override
	public boolean isCreateCostEnabled() {
		return getConfig().getBoolean("on-create.enable-cost", true);
	}

}
