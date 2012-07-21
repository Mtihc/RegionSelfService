package com.mtihc.minecraft.regionselfservice.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.regionselfservice.RentSession;

public class SignsRentYaml extends SignsAbstract {

	private static final String KEY_HOURS_MAX = "hoursMax";
	private static final String KEY_HOURS_TOTAL = "hoursTotal";
	private static final String KEY_HOURS_REMAINING = "hoursRemaining";
	private static final String KEY_RENTERS = "renters";
	
	public SignsRentYaml(JavaPlugin plugin) {
		super(plugin, "signs-rent");
	}

	public double getRegionMaxHours(String world, String region) {
		String worldName = world.replace(" ", "_");
		return getConfig().getDouble(worldName + "." + region + "." + KEY_HOURS_MAX, 1);
	}
	
	public void setRegionMaxHours(String world, String region, int hours) {
		String worldName = world.replace(" ", "_");
		getConfig().set(worldName + "." + region + "." + KEY_HOURS_MAX, hours);
	}
	
	public void setRentSession(RentSession session) {
		String worldName = session.getWorld().replace(" ", "_");
		String path = worldName + "." + session.getRegion();
		ConfigurationSection regionSection = getConfig().getConfigurationSection(path);
		if(regionSection == null) {
			regionSection = getConfig().createSection(worldName + "." + session.getRegion());
		}
		path = KEY_RENTERS + "." + session.getPlayerName();
		ConfigurationSection player = regionSection.getConfigurationSection(path);
		if(player == null) {
			player = regionSection.createSection(path);
		}
		player.set(KEY_HOURS_TOTAL, session.getHoursTotal());
		player.set(KEY_HOURS_REMAINING, session.getHoursRemaining());
	}
	
	public Set<String> getRentersOf(String world, String region) {
		try {
			String worldName = world.replace(" ", "_");
			Set<String> renters = getConfig().getConfigurationSection(worldName + "." + region + "." + KEY_RENTERS).getKeys(false);
			return renters;
		}
		catch(NullPointerException e) {
			return null;
		}
		
	}
	
	public List<RentSession> getRentSessions() {
		YamlConfiguration config = getConfig();
		ArrayList<RentSession> result = new ArrayList<RentSession>();
		Set<String> worldNames = config.getKeys(false);
		if(worldNames == null) {
			return result;
		}
		for (String worldName : worldNames) {
			ConfigurationSection worldSection = config.getConfigurationSection(worldName);
			Set<String> regionNames = worldSection.getKeys(false);
			if(regionNames == null) {
				continue;
			}
			for (String regionName : regionNames) {
				ConfigurationSection rentersSection = worldSection.getConfigurationSection(regionName + "." + KEY_RENTERS);
				if(rentersSection == null) {
					continue;
				}
				Set<String> playerNames = rentersSection.getKeys(false);
				if(playerNames == null) {
					continue;
				}
				for (String playerName : playerNames) {
					ConfigurationSection player = rentersSection.getConfigurationSection(playerName);
					int hoursTotal = player.getInt(KEY_HOURS_TOTAL, 1);
					int hoursRemaining = player.getInt(KEY_HOURS_REMAINING, 1);
					RentSession session = new RentSession(getPlugin(), playerName, worldName, regionName, hoursTotal, hoursTotal - hoursRemaining);
					result.add(session);
				}
			}
		}
		return result;
	}
	

	public void setMemberRemainingHours(String worldName, String regionName,
			String playerName, int hoursRemaining) {
		YamlConfiguration config = getConfig();
		worldName = worldName.replace(" ", "_");
		String path = worldName + "." + regionName + "." + KEY_RENTERS + "." + playerName;
		ConfigurationSection player = config.getConfigurationSection(path);
		player.set(KEY_HOURS_REMAINING, hoursRemaining);
	}

	public void clearMember(String worldName, String regionName,
			String playerName) {
		YamlConfiguration config = getConfig();
		worldName = worldName.replace(" ", "_");
		ConfigurationSection region = config.getConfigurationSection(worldName + "." + regionName);
		ConfigurationSection renters = region.getConfigurationSection(KEY_RENTERS);
		if(renters == null) {
			return;
		}
		renters.set(playerName, null);
		if(renters.getKeys(false).size() == 0) {
			if(region.getList(SignsSaleYaml.KEY_SIGNS_LIST, null) == null) {
				clearRegion(worldName, regionName);
			}
			else {
				region.set(KEY_RENTERS, null);
			}
		}
	}
}
