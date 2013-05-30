package com.mtihc.regionselfservice.v2.plots.signs;

import org.bukkit.configuration.serialization.DelegateDeserialization;

import com.mtihc.regionselfservice.v2.plots.IPlotSign;
import com.mtihc.regionselfservice.v2.plots.Plot;

@DelegateDeserialization(ForRentSignData.class)
public class ForRentSign extends ForRentSignData implements IPlotSign {

	private Plot plot;

	public ForRentSign(Plot plot, ForRentSignData data) {
		super(data);
		this.plot = plot;
	}
	
	@Override
	public Plot getPlot() {
		return plot;
	}

}
