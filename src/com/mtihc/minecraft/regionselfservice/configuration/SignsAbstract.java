package com.mtihc.minecraft.regionselfservice.configuration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.mtihc.minecraft.core1.YamlFile;

public abstract class SignsAbstract extends YamlFile {

	public static final String KEY_SIGNS_LIST = "signs";
	public static final String KEY_SIGN_PREFIX = "sign";
	public static final String KEY_COST = "cost";
	
	public SignsAbstract(JavaPlugin plugin, String name) {
		super(plugin, name);
	}


	public boolean hasRegion(String world, String region) {
		String worldName = world.replace(" ", "_");
		return getConfig().contains(worldName + "." + region);
	}
	
	public double getRegionCost(String world, String region) {
		String worldName = world.replace(" ", "_");
		return getConfig().getDouble(worldName + "." + region + "." + KEY_COST, -1);
	}
	
	public void setRegionCost(String world, String region, double cost) {
		String worldName = world.replace(" ", "_");
		getConfig().set(worldName + "." + region + "." + KEY_COST, cost);
	}
	
	public List<Vector> getRegionSigns(String world, String region) {
		List<String> signNames = getRegionSignNames(world, region);
		List<Vector> result = new ArrayList<Vector>();
		for (String signName : signNames) {
			result.add(convertStringToVector(signName));
		}
		return result;
	}

	public void setSign(String world, String region, int signX, int signY, int signZ) {
		List<String> signNames = getRegionSignNames(world, region);
		String signName = convertVectorToString(KEY_SIGN_PREFIX, signX, signY, signZ);
		if(!signNames.contains(signName)) {
			signNames.add(signName);
		}
		String worldName = world.replace(" ", "_");
		getConfig().set(worldName + "." + region + "." + KEY_SIGNS_LIST, signNames);
	}
	
	public void clearRegion(String world, String region) {
		String worldName = world.replace(" ", "_");
		getConfig().set(worldName + "." + region, null);
	}
	
	public void clearRegionSign(String world, String region, int signX, int signY, int signZ) {
		List<String> signNames = getRegionSignNames(world, region);
		String signName = convertVectorToString(KEY_SIGN_PREFIX, signX, signY, signZ);
		int index = signNames.indexOf(signName);
		if(index > -1) {
			signNames.remove(index);
		}
		if(signNames.size() == 0) {
			signNames = null;
			clearRegion(world, region);
		}
		else {
			setRegionSignNames(world, region, signNames);
		}
	}
	
	
	public List<String> getRegionSignNames(String world, String region) {
		String worldName = world.replace(" ", "_");
		return getConfig().getStringList(worldName + "." + region + "." + KEY_SIGNS_LIST);
	}
	
	private void setRegionSignNames(String world, String region, List<String> signNames) {
		String worldName = world.replace(" ", "_");
		getConfig().set(worldName + "." + region + "." + KEY_SIGNS_LIST, signNames);
	}
	
	public String convertVectorToString(String prefix, int x, int y, int z) {
		return prefix + "_" + x + "_" + y + "_" + z;
	}
	
	public Vector convertStringToVector(String string) {
		String[] split = string.split("_");
		int x = Integer.parseInt(split[split.length - 3]);
		int y = Integer.parseInt(split[split.length - 2]);
		int z = Integer.parseInt(split[split.length - 1]);
		return new Vector(x, y, z);
	}
}
