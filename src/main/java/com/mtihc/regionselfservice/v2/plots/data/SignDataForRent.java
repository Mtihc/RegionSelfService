package com.mtihc.regionselfservice.v2.plots.data;

import java.util.Map;

import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

public class SignDataForRent extends SignData {

	private String renter;
	private long timeLeft;
	private double costPerHour;

	
	public SignDataForRent(SignDataForRent other) {
		this(other.getBlockVector(), other.getAttachedFace(), other.getRegionId(), other.getCostPerHour());
	}
	
	public SignDataForRent(BlockVector coords,
			BlockFace attachedFace, String regionId, double costPerHour) {
		super(SignType.FOR_RENT, coords, attachedFace, regionId);
		this.renter = null;
		this.timeLeft = 0;
		this.costPerHour = costPerHour;
	}

	public SignDataForRent(Map<String, Object> values) {
		super(values);
		this.renter = (String) values.get("renter");
		this.timeLeft = (Long) values.get("time-left");
		this.costPerHour = (Double) values.get("cost");
	}

	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = super.serialize();
		values.put("renter", renter);
		values.put("time-left", timeLeft);
		return values;
	}
	
	public double getCostPerHour() {
		return costPerHour;
	}
	
	public void setCostPerHour(double costPerHour) {
		this.costPerHour = costPerHour;
	}
	
	public boolean hasRenter() {
		return renter != null;
	}
	
	public String getRenterName() {
		return renter;
	}
	
	public void setRenterName(String name) {
		this.renter = name;
	}
	
	public long getTimeLeft() {
		return timeLeft;
	}
	
	public void setTimeLeft(long timeLeft) {
		this.timeLeft = timeLeft;
	}
}
