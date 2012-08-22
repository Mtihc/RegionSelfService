package com.mtihc.regionselfservice.v2.plots;

import java.util.Collection;
import java.util.Set;

import com.mtihc.regionselfservice.v2.plots.data.PlotData;

public interface IPlotDataRepository {

	public abstract PlotData get(String id);
	public abstract void set(String id, PlotData data);
	public abstract void remove(String id);
	public abstract boolean has(String id);
	public abstract Collection<PlotData> getValues();
	public abstract Set<String> getIds();
	
}
