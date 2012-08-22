package com.mtihc.regionselfservice.v2.plots;

public interface IPlotWorldConfig {

	public abstract String getWorldName();
	
	public abstract double getBlockWorth();
	
	public abstract double getOnSellMinBlockCost();
	
	public abstract double getOnSellMaxBlockCost();
	
	public abstract double getOnRentMinBlockCost();
	
	public abstract double getOnRentMaxBlockCost();
	
}
