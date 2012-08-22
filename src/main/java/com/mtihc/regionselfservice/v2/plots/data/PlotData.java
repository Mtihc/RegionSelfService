package com.mtihc.regionselfservice.v2.plots.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.BlockVector;

public class PlotData implements ConfigurationSerializable {

	protected final World world;
	protected final String regionId;
	protected final Map<BlockVector, ISignData> signs;
	
	public PlotData(World world, String regionId) {
		this(world, regionId, null);
	}
	
	public PlotData(World world, String regionId, Map<BlockVector, ISignData> signs) {
		this.world = world;
		this.regionId = regionId;
		if(signs != null) {
			this.signs = signs;
		}
		else {
			this.signs = new HashMap<BlockVector, ISignData>();
		}
	}
	
	public PlotData(PlotData other) {
		this.world = other.world;
		this.regionId = other.regionId;
		this.signs = other.signs;
	}
	
	public PlotData(Map<String, Object> values) {
		this.world = Bukkit.getWorld(
				(String) values.get("world"));
		this.regionId = (String) values.get("region");
		
		this.signs = new HashMap<BlockVector, ISignData>();
		Map<?, ?> signSection = (Map<?, ?>) values.get("signs");
		
		if(signSection != null) {
			Collection<?> signValues = signSection.values();
			for (Object value : signValues) {
				if(!(value instanceof ISignData)) {
					continue;
				}
				ISignData signData = (ISignData) value;
				this.signs.put(signData.getBlockVector(), signData);
			}
			
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		
		values.put("world", world.getName());
		values.put("region", regionId);
		
		Map<String, Object> signSection = new LinkedHashMap<String, Object>();
		Collection<ISignData> signValues = signs.values();
		int index = 0;
		for (ISignData signData : signValues) {
			signSection.put("sign" + index, signData);
			index++;
		}
		values.put("signs", signSection);
		
		return values;
	}
	
	public String getRegionId() {
		return regionId;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Collection<ISignData> getSigns() {
		return signs.values();
	}
	
	public Collection<ISignData> getSigns(SignType type) {
		Collection<ISignData> result = new ArrayList<ISignData>();
		Collection<ISignData> values = signs.values();
		for (ISignData data : values) {
			if(data.getSignType() == type) {
				result.add(data);
			}
		}
		return result;
	}
	
	public Set<BlockVector> getSignLocations() {
		return signs.keySet();
	}
	
	public void setSign(ISignData sign) {
		signs.put(sign.getBlockVector(), sign);
	}
	
	public ISignData getSign(BlockVector coords) {
		return signs.get(coords);
	}
	
	public boolean hasSign(BlockVector coords) {
		return signs.containsKey(coords);
	}
	
	public void removeSign(BlockVector coords) {
		signs.remove(coords);
	}
	

}
