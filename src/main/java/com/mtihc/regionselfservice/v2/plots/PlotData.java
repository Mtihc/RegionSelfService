package com.mtihc.regionselfservice.v2.plots;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.BlockVector;

import com.mtihc.regionselfservice.v2.plots.signs.IPlotSignData;
import com.mtihc.regionselfservice.v2.plots.signs.PlotSignType;

public class PlotData implements ConfigurationSerializable {
	
	protected final World world;
	protected final String regionId;
	private double sellCost = 0;
	private double rentCost = 0;
	protected final Map<BlockVector, IPlotSignData> signs = new HashMap<BlockVector, IPlotSignData>();
	
	public PlotData(World world, String regionId, double sellCost, double rentCost) {
		this(world, regionId, sellCost, rentCost, null);
	}
	
	public PlotData(World world, String regionId, double sellCost, double rentCost, Collection<IPlotSignData> signs) {
		this.world = world;
		this.regionId = regionId;
		this.sellCost = sellCost;
		this.rentCost = rentCost;
		
		if(signs != null) {
			for (IPlotSignData data : signs) {
				setSign(data);
			}
		}
	}

	
	public PlotData(Map<String, Object> values) {
		this.world = Bukkit.getWorld(
				(String) values.get("world"));
		this.regionId = (String) values.get("region-id");
		this.sellCost = (Double) values.get("sell-cost");
		this.rentCost = (Double) values.get("rent-cost");
		
		Map<?, ?> signsSection = (Map<?, ?>) values.get("signs");
		if(signsSection != null) {
			Collection<?> signsValues = signsSection.values();
			for (Object object : signsValues) {
				if(!(object instanceof IPlotSignData)) {
					continue;
				}
				IPlotSignData data = (IPlotSignData) object;
				setSign(data);
			}
		}
		
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		
		values.put("world", world.getName());
		values.put("region-id", regionId);
		values.put("sell-cost", sellCost);
		values.put("rent-cost", rentCost);

		Map<String, Object> signsSection = new LinkedHashMap<String, Object>();
		Collection<IPlotSignData> signs = getSigns();
		int index = 0;
		for (IPlotSignData data : signs) {
			signsSection.put("sign" + index, data);
			index++;
		}
		values.put("signs", signsSection);
		
		return values;
	}
	
	public World getWorld() {
		return world;
	}
	
	public boolean isForSale() {
		return hasSign(PlotSignType.FOR_SALE);
	}
	
	public double getSellCost() {
		return sellCost;
	}
	
	public void setSellCost(double cost) {
		this.sellCost = cost;
		Collection<IPlotSignData> values = signs.values();
		for (IPlotSignData value : values) {
			if(value.getType() != PlotSignType.FOR_SALE) {
				continue;
			}
			Location loc = value.getBlockVector().toLocation(world);
			Block block = loc.getBlock();
			if(block.getState() instanceof Sign) {
				Sign sign = (Sign) block.getState();
				sign.setLine(1, String.valueOf(cost));
			}
		}
	}
	
	public boolean isForRent() {
		return hasSign(PlotSignType.FOR_RENT);
	}
	
	public double getRentCost() {
		return rentCost;
	}
	
	public void setRentCost(double cost) {
		this.rentCost = cost;
		Collection<IPlotSignData> values = signs.values();
		for (IPlotSignData value : values) {
			if(value.getType() != PlotSignType.FOR_RENT) {
				continue;
			}
			Location loc = value.getBlockVector().toLocation(world);
			Block block = loc.getBlock();
			if(block.getState() instanceof Sign) {
				Sign sign = (Sign) block.getState();
				sign.setLine(1, String.valueOf(cost));// TODO add time?
			}
		}
	}
	
	public String getRegionId() {
		return regionId;
	}
	
	
	public IPlotSignData getSign(BlockVector coords) {
		return signs.get(coords);
	}
	
	public void setSign(IPlotSignData data) {
		signs.put(data.getBlockVector(), data);
		
	}
	
	
	public boolean hasSign(BlockVector coords) {
		return signs.containsKey(coords);
	}
	
	public boolean hasSign(PlotSignType<?> type) {
		Collection<IPlotSignData> values = signs.values();
		for (IPlotSignData value : values) {
			if(value == null) {
				continue;
			}
			if(value.getType() == type) {
				return true;
			}
		}
		return false;
	}
	
	public IPlotSignData removeSign(BlockVector coords) {
		return signs.remove(coords);
	}
	
	public Collection<IPlotSignData> getSigns() {
		return getSigns(null);
	}
	
	public Collection<IPlotSignData> getSigns(PlotSignType<?> type) {
		Collection<IPlotSignData> values = signs.values();
		ArrayList<IPlotSignData> result = new ArrayList<IPlotSignData>();
		for (IPlotSignData value : values) {
			if(value == null) {
				continue;
			}
			if(type == null || value.getType() == type) {
				result.add(value);
			}
		}
		return result;
	}
}
