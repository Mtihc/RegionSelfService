package com.mtihc.regionselfservice.v2.plots.signs;

import java.util.Map;

import org.bukkit.util.BlockVector;



public class ForRentSignData extends PlotSignData {

	public ForRentSignData(ForRentSignData other) {
		super(other);
	}

	public ForRentSignData(PlotSignType<?> type, BlockVector coords) {
		super(type, coords);
	}

	public ForRentSignData(Map<String, Object> values) {
		super(values);
	}

	/* (non-Javadoc)
	 * @see com.mtihc.regionselfservice.v2.plots.PlotSignData#serialize()
	 */
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = super.serialize();
		
		return values;
	}
	
	

}
