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


public abstract class PlotDataRepository extends Repository<String, PlotData> implements IPlotDataRepository {
    
    public PlotDataRepository(String directory) {
	this(new File(directory), null);
    }
    
    public PlotDataRepository(File directory) {
	this(directory, null);
    }
    
    public PlotDataRepository(String directory, Logger logger) {
	this(new File(directory), logger);
    }
    
    public PlotDataRepository(File directory, Logger logger) {
	super(directory, logger);
    }
    
    @Override
    protected abstract String getPathByKey(String regionId);
    
    @Override
    public Collection<PlotData> getValues() {
	Set<String> ids = getIds();
	ArrayList<PlotData> values = new ArrayList<PlotData>();
	for (String id : ids) {
	    PlotData data = get(id);
	    if (data == null) {
		continue;
	    }
	    values.add(data);
	}
	return values;
    }
    
    @Override
    public Set<String> getIds() {
	final HashSet<String> ids = new HashSet<String>();
	
	this.directory.list(new FilenameFilter() {
	    
	    @Override
	    public boolean accept(File dir, String name) {
		if (!name.endsWith(".yml")) {
		    return false;
		}
		String id = name.substring(0, name.length() - ".yml".length());
		ids.add(id);
		return false;
	    }
	});
	return ids;
    }
    
}
