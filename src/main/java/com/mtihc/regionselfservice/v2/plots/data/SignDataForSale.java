package com.mtihc.regionselfservice.v2.plots.data;

import java.util.Map;

import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

public class SignDataForSale extends SignData {

	private double cost;

	public SignDataForSale(SignDataForSale other) {
		this(other.getBlockVector(), other.getAttachedFace(), other.getCost());
	}
	
	public SignDataForSale(BlockVector coords, BlockFace attachedFace, double cost) {
		super(SignType.FOR_SALE, coords, attachedFace);
		this.cost = cost;
	}

	public SignDataForSale(Map<String, Object> values) {
		super(values);
		this.cost = (Double) values.get("cost");
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = super.serialize();
		values.put("cost", cost);
		return values;
	}
	
	public double getCost() {
		return cost;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
}
