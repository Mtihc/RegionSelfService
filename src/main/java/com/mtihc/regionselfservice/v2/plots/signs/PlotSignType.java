package com.mtihc.regionselfservice.v2.plots.signs;

import java.util.Arrays;
import java.util.List;

import org.bukkit.block.Sign;

import com.mtihc.regionselfservice.v2.plots.Plot;
import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;

public abstract class PlotSignType<T extends IPlotSign> {
	
	public static final ForRent FOR_RENT = new ForRent();
	public static final ForSale FOR_SALE = new ForSale();
	
	public static PlotSignType<?>[] values() {
		return new PlotSignType[] {FOR_RENT, FOR_SALE};
	}

	public static PlotSignType<?> valueOf(String name) {
		PlotSignType<?>[] values = values();
		for (PlotSignType<?> value : values) {
			if(value.name.equalsIgnoreCase(name)) {
				return value;
			}
		}
		return null;
	}
	
	public static PlotSignType<?> getPlotSignType(Sign sign, String[] lines) {
		PlotSignType<?>[] values = values();
		for (PlotSignType<?> value : values) {
			if(value.isValidPlotSign(sign, lines)) {
				return value;
			}
		}
		return null;
	}
	
	public static String getRegionId(Sign sign, String[] lines) {
		String result = "";
		for (int i = 2; i < lines.length; i++) {
			result += lines[i].trim();
		}
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	private String name;
	
	private String[] defaultFirstLineOptions;
	
	private List<String> firstLineOptions;
	
	
	
	
	
	protected PlotSignType(String name, String[] firstLineOptions) {
		this.name = name;
		this.defaultFirstLineOptions = firstLineOptions;
		resetDefaultFirstLineOptions();
	}
	

	public String name() {
		return name;
	}
	
	
	
	

	public abstract T createPlotSign(Plot plot, IPlotSignData data) throws SignException;
	
	public abstract T createPlotSign(Plot plot, Sign sign, String[] lines) throws SignException;
	
	public boolean isValidPlotSign(Sign sign, String[] lines) {
		if(lines == null || lines.length < 4) {
			return false;
		}
		String line = lines[0].trim();
		for (String string : firstLineOptions) {
			if(string.equalsIgnoreCase(line)) {
				lines[0] = string;
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	public List<String> getFirstLineOptions() {
		return firstLineOptions;
	}
	
	public void resetDefaultFirstLineOptions() {
		this.firstLineOptions = Arrays.asList(defaultFirstLineOptions);
	}
	
	public void setFirstLineOptions(List<String> firstLineOptions) {
		this.firstLineOptions = firstLineOptions;
		if(firstLineOptions == null || firstLineOptions.isEmpty()) {
			resetDefaultFirstLineOptions();
		}
		else {
			this.firstLineOptions = firstLineOptions;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == this) {
			return true;
		}
		if(other == null) {
			return false;
		}
		if(!(other instanceof PlotSignType)) {
			return false;
		}
		PlotSignType<?> type = (PlotSignType<?>) other;
		if(type.name.equals(name)) {
			return true;
		}
		return false;
	}
	
}
