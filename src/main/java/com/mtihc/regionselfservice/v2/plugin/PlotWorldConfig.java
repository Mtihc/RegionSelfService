package com.mtihc.regionselfservice.v2.plugin;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import com.mtihc.regionselfservice.v2.plots.IPlotWorldConfig;
import com.mtihc.regionselfservice.v2.plugin.util.YamlFile;


public class PlotWorldConfig extends YamlFile implements IPlotWorldConfig {
    
    public PlotWorldConfig(String filePath) {
	this(new File(filePath), null);
    }
    
    public PlotWorldConfig(File file) {
	this(file, null);
    }
    
    public PlotWorldConfig(String filePath, Logger logger) {
	this(new File(filePath), logger);
    }
    
    public PlotWorldConfig(File file, Logger logger) {
	super(file, logger);
    }
    
    @Override
    public double getBlockWorth() {
	return getConfig().getDouble("block_worth");
    }
    
    public void setBlockWorth(double value) {
	getConfig().set("block_worth", value);
    }
    
    @Override
    public double getOnSellMinBlockCost() {
	return getConfig().getDouble("sell_min_block_cost");
    }
    
    public void setOnSellMinBlockCost(double value) {
	getConfig().set("sell_min_block_cost", value);
    }
    
    @Override
    public double getOnSellMaxBlockCost() {
	return getConfig().getDouble("sell_max_block_cost");
    }
    
    public void setOnSellMaxBlockCost(double value) {
	getConfig().set("sell_max_block_cost", value);
    }
    
    @Override
    public double getOnRentMinBlockCost() {
	return getConfig().getDouble("rent_min_block_cost");
    }
    
    public void setOnRentMinBlockCost(double value) {
	getConfig().set("rent_min_block_cost", value);
    }
    
    @Override
    public double getOnRentMaxBlockCost() {
	return getConfig().getDouble("rent_max_block_cost");
    }
    
    public void setOnRentMaxBlockCost(double value) {
	getConfig().set("rent_max_block_cost", value);
    }
    
    @Override
    public int getMaxRegionCount() {
	return getConfig().getInt("max_regions_per_player");
    }
    
    public void setMaxRegionCount(int value) {
	getConfig().set("max_regions_per_player", value);
    }
    
    @Override
    public boolean isReserveFreeRegionsEnabled() {
	return getConfig().getBoolean("reserve_free_regions");
    }
    
    public void setReserveFreeRegionsEnabled(boolean enabled) {
	getConfig().set("reserve_free_regions", enabled);
    }
    
    @Override
    public int getMinimumY() {
	return getConfig().getInt("region_size.minimum_y");
    }
    
    public void setMinimumY(int value) {
	getConfig().set("region_size.minimum_y", value);
    }
    
    @Override
    public int getMaximumY() {
	return getConfig().getInt("region_size.maximum_y");
    }
    
    public void setMaximumY(int value) {
	getConfig().set("region_size.maximum_y", value);
    }
    
    @Override
    public int getMinimumHeight() {
	return getConfig().getInt("region_size.minimum_height");
    }
    
    public void setMinimumHeight(int value) {
	getConfig().set("region_size.minimum_height", value);
    }
    
    @Override
    public int getMaximumHeight() {
	return getConfig().getInt("region_size.maximum_height");
    }
    
    public void setMaximumHeight(int value) {
	getConfig().set("region_size.maximum_height", value);
    }
    
    @Override
    public int getMinimumWidthLength() {
	return getConfig().getInt("region_size.minimum_width_length");
    }
    
    public void setMinimumWidthLength(int value) {
	getConfig().set("region_size.minimum_width_length", value);
    }
    
    @Override
    public int getMaximumWidthLength() {
	return getConfig().getInt("region_size.maximum_width_length");
    }
    
    public void setMaximumWidthLength(int value) {
	getConfig().set("region_size.maximum_width_length", value);
    }
    
    @Override
    public int getDefaultBottomY() {
	return getConfig().getInt("region_defaults.bottom_y");
    }
    
    public void setDefaultBottomY(int value) {
	getConfig().set("region_defaults.bottom_y", value);
    }
    
    @Override
    public int getDefaultTopY() {
	return getConfig().getInt("region_defaults.top_y");
    }
    
    public void setDefaultTopY(int value) {
	getConfig().set("region_defaults.top_y", value);
    }
    
    @Override
    public List<String> getDefaultOwners() {
	return getConfig().getStringList("region_defaults.owners");
    }
    
    public void setDefaultOwners(List<String> owners) {
	getConfig().set("region_defaults.owners", owners);
    }
    
    @Override
    public boolean isOverlapUnownedRegionAllowed() {
	return getConfig().getBoolean("allow_overlap_unowned_regions");
    }
    
    public void setOverlapUnownedRegionAllowed(boolean allowed) {
	getConfig().set("allow_overlap_unowned_regions", allowed);
    }
    
    @Override
    public boolean isAutomaticParentEnabled() {
	return getConfig().getBoolean("region_defaults.parent_automatic");
    }
    
    public void setAutomaticParentEnabled(boolean enabled) {
	getConfig().set("region_defaults.parent_automatic", enabled);
    }
    
    @Override
    public boolean isCreateCostEnabled() {
	return getConfig().getBoolean("enable_create_cost");
    }
    
    public void setCreateCostEnabled(boolean enabled) {
	getConfig().set("enable_create_cost", enabled);
    }
    
    @Override
    public String getTaxAccount() {
	return getConfig().getString("tax_to_account");
    }
    
    public void setTaxAccount(String name) {
	getConfig().set("tax_to_account", name);
    }
    
    @Override
    public double getTaxPercent() {
	return getConfig().getDouble("tax_percent");
    }
    
    public void setTaxPercent(double value) {
	getConfig().set("tax_percent", value);
    }
    
    @Override
    public double getTaxFromPrice() {
	return getConfig().getDouble("tax_from_price");
    }
    
    public void setTaxFromPrice(double value) {
	getConfig().set("tax_from_price", value);
    }
    
    @Override
    public double getDeleteRefundPercent() {
	return getConfig().getDouble("percent_delete_refund");
    }
    
    public void setDeleteRefundPercent(double value) {
	getConfig().set("percent_delete_refund", value);
    }
    
    @Override
    public double getAllowRentExtendAfterPercentTime() {
	return getConfig().getDouble("allow_rent_extend_after_percent_time");
    }
    
    public void setAllowRentExtendAfterPercentTime(double value) {
	getConfig().set("allow_rent_extend_after_percent_time", value);
    }
    
}
