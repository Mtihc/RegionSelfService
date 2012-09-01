package com.mtihc.regionselfservice.v2.plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.mtihc.regionselfservice.v2.plots.IPlotDataRepository;
import com.mtihc.regionselfservice.v2.plots.PlotData;
import com.mtihc.regionselfservice.v2.plugin.util.Repository;

public class PlotDataRepository extends Repository<String, PlotData> implements
		IPlotDataRepository {

	private String worldName;

	public PlotDataRepository(String directory, String worldName) {
		this(new File(directory), worldName, null);
	}

	public PlotDataRepository(File directory, String worldName) {
		this(directory, worldName, null);
	}

	public PlotDataRepository(String directory, String worldName, Logger logger) {
		this(new File(directory), worldName, logger);
	}

	public PlotDataRepository(File directory, String worldName, Logger logger) {
		super(new File(directory + "/" + worldName), logger);
		this.worldName = worldName;
	}
	
	public String getWorldName() {
		return worldName;
	}

	@Override
	public Collection<PlotData> getValues() {
		Set<String> ids = getIds();
		ArrayList<PlotData> values = new ArrayList<PlotData>();
		for (String id : ids) {
			PlotData data = get(id);
			if(data == null) {
				continue;
			}
			values.add(data);
		}
		return values;
	}

	@Override
	public Set<String> getIds() {
		final HashSet<String> ids = new HashSet<String>();
		
		directory.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if(!name.endsWith(".yml")) {
					return false;
				}
				String id = name.substring(0, name.length() - ".yml".length());
				ids.add(id);
				return false;
			}
		});
		return ids;
	}

	@Override
	protected String getPathByKey(String key) {
		return directory + "/" + key + ".yml";
	}

}
