package com.mtihc.regionselfservice.v2.plots.signs;

import org.bukkit.configuration.serialization.DelegateDeserialization;

import com.mtihc.regionselfservice.v2.plots.Plot;

@DelegateDeserialization(ForSaleSignData.class)
public class ForSaleSign extends ForSaleSignData implements IPlotSign {

	private Plot plot;

	public ForSaleSign(Plot plot, ForSaleSignData data) {
		super(data);
		this.plot = plot;
	}
	
	@Override
	public Plot getPlot() {
		return plot;
	}

}
