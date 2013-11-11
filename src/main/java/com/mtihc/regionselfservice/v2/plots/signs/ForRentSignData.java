package com.mtihc.regionselfservice.v2.plots.signs;

import java.util.Map;

import org.bukkit.util.BlockVector;



public class ForRentSignData extends PlotSignData {

	private String rentPlayer;
	private long rentPlayerTime;
	
	public ForRentSignData(ForRentSignData other) {
		super(other);
		this.rentPlayer = other.rentPlayer;
		this.rentPlayerTime = other.rentPlayerTime;
	}

	public ForRentSignData(PlotSignType<?> type, BlockVector coords) {
		super(type, coords);
		// when sign is created, nobody is renting yet
		this.rentPlayer = null;
		this.rentPlayerTime = 0;
	}

	public ForRentSignData(Map<String, Object> values) {
		super(values);
		this.rentPlayer = (String) values.get("rent-player");
		this.rentPlayerTime = (Long) values.get("rent-player-time");
	}

	/* (non-Javadoc)
	 * @see com.mtihc.regionselfservice.v2.plots.PlotSignData#serialize()
	 */
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = super.serialize();
		values.put("rent-player", rentPlayer);
		values.put("rent-player-time", rentPlayerTime);
		return values;
	}
	
	public String getRentPlayer() {
		return rentPlayer;
	}
	
	public void setRentPlayer(String playerName) {
		this.rentPlayer = playerName;
	}
	
	public long getRentPlayerTime() {
		return rentPlayerTime;
	}
	
	public void setRentPlayerTime(long millisec) {
		this.rentPlayerTime = millisec;
	}
	

}
