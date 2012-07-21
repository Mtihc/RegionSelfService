package com.mtihc.minecraft.regionselfservice.configuration;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.regionselfservice.core.YamlFile;

public class ConfigYaml extends YamlFile {

	
	public ConfigYaml(JavaPlugin plugin) {
		super(plugin, "config");
	}


	public List<String> getFirstLineForRent() {
		return getConfig().getStringList("firstLineForRent");
	}
	
	public List<String> getFirstLineForSale() {
		return getConfig().getStringList("firstLineForSale");
	}

	/**
	 * Returns the default minimum y-coördinate of a region, as defined in the
	 * configuration file.
	 * 
	 * @return The default minimum y-coördinate of a region.
	 */
	public int getDefaultBottomY() {
		return (int) getConfig().getDouble("onCreate.defaultBottomY", 0);
	}

	/**
	 * Returns the default maximum y-coördinate of a region, as defined in the
	 * configuration file.
	 * 
	 * @return The default maximum y-coördinate of a region.
	 */
	public int getDefaultTopY() {
		return (int) getConfig().getDouble("onCreate.defaultTopY", 127);
	}
	
	public boolean automaticParent() {
		return getConfig().getBoolean("onCreate.automaticParent", false);
	}

	/**
	 * The maximum amount of regions per player, as defined in the configuration
	 * file
	 * 
	 * @return Maximum amount of regions per player
	 */
	public int getMaxRegionsPerPlayer() {
		return (int) getConfig().getDouble("maxRegionsPerPlayer", 1);
	}
	
	public double getBlockWorth() {
		return getConfig().getDouble("blockWorth", 0.22);
	}
	
	public double getOnSellMinBlockCost() {
		return getConfig().getDouble("onSell.minBlockCost", 0.20);
	}
	
	public double getOnSellMaxBlockCost() {
		return getConfig().getDouble("onSell.maxBlockCost");
	}
	

	public double getOnRentMinBlockCost() {
		return getConfig().getDouble("onRent.minBlockCost");
	}
	
	public boolean getOnBuyReserveFreeRegions() {
		return getConfig().getBoolean("onBuy.reserveFreeRegions", true);
	}
	
	public double getOnRentMaxBlockCost() {
		return getConfig().getDouble("onRent.maxBlockCost");
	}
	
	public int getOnRentMaxHours() {
		return (int) getConfig().getDouble("onRent.maxHours", 1);
	}

	public List<String> getDefaultOwners() {
		return getConfig().getStringList("onCreate.defaultOwners");
	}

	public boolean getEnableOnCreateCost() {
		return getConfig().getBoolean("onCreate.enableCost", false);
	}
	
	public int getMinimumWidthLength() {
		return (int) getConfig().getDouble("onCreate.sizes.minimumWidthLength", 8);
	}
	public int getMaximumWidthLength() {
		return (int) getConfig().getDouble("onCreate.sizes.maximumWidthLength", 100);
	}
	
	public int getMinimumY() {
		return (int) getConfig().getDouble("onCreate.sizes.minimumY", 0);
	}
	
	public int getMaximumY() {
		return (int) getConfig().getDouble("onCreate.sizes.maximumY", 8);
	}
	
	public int getMinimumHeight() {
		return (int) getConfig().getDouble("onCreate.sizes.minimumHeight", 8);
	}
	
	public int getMaximumHeight() {
		return (int) getConfig().getDouble("onCreate.sizes.maximumHeight", 127);
	}

	public int getOnDeleteRefundPercent() {
		return (int) getConfig().getDouble("onDelete.refundPercent", 0);
	}


	public boolean allowOverlapUnownedRegions() {
		return getConfig().getBoolean("onCreate.allowOverlapUnownedRegions", false);
	}


	public String getTaxAccount() {
		return getConfig().getString("onSell.tax.account", "Mtihc");
	}


	public double getTaxFromPrice() {
		return getConfig().getDouble("onSell.tax.from-price", 100);
	}


	public int getTaxPercent() {
		return getConfig().getInt("onSell.tax.percent", 19);
	}
}
