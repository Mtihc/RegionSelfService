package com.mtihc.minecraft.regionselfservice;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RentSession implements Runnable {
	
	private JavaPlugin plugin;
	private int hoursCurrent;
	private int hoursTotal;
	private int taskId;
	
	private String playerName;
	private String worldName;
	private String regionName;
	
	private RentSessionObserver observer;
	
	public RentSession(JavaPlugin plugin, String playerName, String worldName, String regionName, int totalHours, int currentHours) {
		this.plugin = plugin;
		this.playerName = playerName;
		this.worldName = worldName;
		this.regionName = regionName;
		this.hoursTotal = totalHours;
		this.hoursCurrent = currentHours;
		this.taskId = -1;
	}
	
	public String getPlayerName() { return playerName; }
	
	public Player getOnlinePlayer() {
		return plugin.getServer().getPlayer(playerName);
	}
	
	public String getWorld() { return worldName; }
	
	public String getRegion() { return regionName; }
	
	public int getHoursTotal() { return hoursTotal; }
	
	public int getHoursRemaining() { return hoursTotal - hoursCurrent; }
	
	public int getHoursCurrent() { return hoursCurrent; }
	
	public RentSession(JavaPlugin plugin, String playerName, String worldName, String regionName, int totalHours) {
		this(plugin, playerName, worldName, regionName, totalHours, 0);
	}
	
	public int getCurrentHours() { return hoursCurrent; }
	
	public int getTotalHours() { return hoursTotal; }

	public void setObserver(RentSessionObserver observer) {
		this.observer = observer;
	}
	
	public RentSessionObserver getObserver() {
		return this.observer;
	}
	
	@Override
	public void run() {
		hoursCurrent++;
		observer.onHourPassed(this);
	}

	public void start() {
		startScheduler();
	}
	
	public void stop() {
		stopScheduler();
	}
	
	private void startScheduler() {
		long hour = 3600 * 20;
		taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, hour, hour);
	}
	
	private void stopScheduler() {
		plugin.getServer().getScheduler().cancelTask(taskId);
		taskId = -1;
	}
}
