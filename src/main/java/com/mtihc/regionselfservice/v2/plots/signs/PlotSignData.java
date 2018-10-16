package com.mtihc.regionselfservice.v2.plots.signs;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.util.BlockVector;

import com.mtihc.regionselfservice.v2.plots.IPlotSignData;


abstract class PlotSignData implements IPlotSignData {
    
    private BlockVector coords;
    private PlotSignType type;
    
    protected PlotSignData(IPlotSignData other) {
	this(other.getType(), other.getBlockVector());
    }
    
    protected PlotSignData(PlotSignType type, BlockVector coords) {
	this.coords = coords.clone();
	this.type = type;
    }
    
    protected PlotSignData(Map<String, Object> values) {
	this.coords = (BlockVector) values.get("coords");
	
	String typeName = (String) values.get("sign-type");
	this.type = PlotSignType.valueOf(typeName);
    }
    
    @Override
    public Map<String, Object> serialize() {
	Map<String, Object> values = new LinkedHashMap<String, Object>();
	values.put("coords", this.coords);
	values.put("sign-type", this.type.name());
	return values;
    }
    
    /* (non-Javadoc)
     * @see com.mtihc.regionselfservice.v2.plots.signs.IPlotSignData#getBlockVector()
     */
    @Override
    public BlockVector getBlockVector() {
	return this.coords.clone();
    }
    
    /* (non-Javadoc)
     * @see com.mtihc.regionselfservice.v2.plots.signs.IPlotSignData#getType()
     */
    @Override
    public PlotSignType getType() {
	return this.type;
    }
    
    /* (non-Javadoc)
     * @see com.mtihc.regionselfservice.v2.plots.signs.IPlotSignData#getTypeName()
     */
    @Override
    public String getTypeName() {
	return this.type.name();
    }
    
}
