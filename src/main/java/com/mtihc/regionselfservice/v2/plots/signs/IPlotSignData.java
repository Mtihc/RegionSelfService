package com.mtihc.regionselfservice.v2.plots.signs;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.BlockVector;

public interface IPlotSignData extends ConfigurationSerializable {

	public abstract BlockVector getBlockVector();

	public abstract PlotSignType<?> getType();

	public abstract String getTypeName();

}