package com.mtihc.regionselfservice.v2.plots;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.BlockVector;

import com.mtihc.regionselfservice.v2.plots.signs.PlotSignType2;

public interface IPlotSignData extends ConfigurationSerializable {

	public abstract BlockVector getBlockVector();

	public abstract PlotSignType2 getType();

	public abstract String getTypeName();

}