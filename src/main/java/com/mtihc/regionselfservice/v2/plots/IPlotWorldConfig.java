package com.mtihc.regionselfservice.v2.plots;

import java.util.List;

public interface IPlotWorldConfig {

	public abstract double getBlockWorth();
	
	public abstract double getOnSellMinBlockCost();
	
	public abstract double getOnSellMaxBlockCost();
	
	public abstract double getOnRentMinBlockCost();
	
	public abstract double getOnRentMaxBlockCost();

	public abstract int getMaxRegionCount();

	public abstract boolean isReserveFreeRegionsEnabled();

	public abstract int getMinimumY();

	public abstract int getMaximumY();

	public abstract int getMinimumHeight();

	public abstract int getMaximumHeight();

	public abstract int getMinimumWidthLength();

	public abstract int getMaximumWidthLength();

	public abstract int getDefaultBottomY();

	public abstract int getDefaultTopY();

	public abstract List<String> getDefaultOwners();

	public abstract boolean isOverlapUnownedRegionAllowed();

	public abstract boolean isAutomaticParentEnabled();

	public abstract boolean isCreateCostEnabled();

	public abstract String getTaxAccount();

	public abstract double getTaxPercent();

	public abstract double getTaxFromPrice();
	
}
