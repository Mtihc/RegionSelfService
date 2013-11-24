package com.mtihc.regionselfservice.v2.plots.signs;

import java.util.Arrays;
import java.util.List;

import com.mtihc.regionselfservice.v2.plots.IPlotSign;
import com.mtihc.regionselfservice.v2.plots.IPlotSignData;
import com.mtihc.regionselfservice.v2.plots.Plot;

public enum PlotSignType2 {

	FOR_RENT("For Rent"), FOR_SALE("For Sale");
	

	private List<String> firstLineOptions;
	private String[] defaultFirstLineOptions;
	
	private PlotSignType2(String name) {
		this.defaultFirstLineOptions = new String[]{name};
		resetFirstLineOptions();
	}
	
	public boolean isFirstLineOption(String firstLine) {
		for (String e : firstLineOptions) {
			if(e.equalsIgnoreCase(firstLine)) {
				return true;
			}
		}
		return false;
	}

	public List<String> getFirstLineOptions() {
		return firstLineOptions;
	}
	
	public void resetFirstLineOptions() {
		firstLineOptions = Arrays.asList(defaultFirstLineOptions);
	}
	
	public void setFirstLineOptions(List<String> firstLineOptions) {
		this.firstLineOptions = firstLineOptions;
		if(firstLineOptions == null || firstLineOptions.isEmpty()) {
			resetFirstLineOptions();
		}
		else {
			this.firstLineOptions = firstLineOptions;
		}
	}

	public static PlotSignType2 getPlotSignType(String[] lines) {
		String firstLine;
		try {
			firstLine = lines[0];
		} catch(Exception e) {
			firstLine = null;
		}
		PlotSignType2[] types = values();
		for (PlotSignType2 type : types) {
			if(type.isFirstLineOption(firstLine)) {
				return type;
			}
		}
		return null;
	}
	
	public static IPlotSign createPlotSign(Plot plot, IPlotSignData data) {
		switch (data.getType()) {
		case FOR_RENT:
			return new ForRentSign(plot, (ForRentSignData) data);
		case FOR_SALE:
			return new ForSaleSign(plot, (ForSaleSignData) data);
		default:
			return null;
		}
	}
}
