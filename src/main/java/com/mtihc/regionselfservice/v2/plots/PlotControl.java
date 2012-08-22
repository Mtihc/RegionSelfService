package com.mtihc.regionselfservice.v2.plots;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlotControl {

	private PlotManager mgr;

	public PlotControl(PlotManager manager) {
		this.mgr = manager;
	}
	
	public PlotManager getPlotManager() {
		return mgr;
	}
	
	public void buy(Player player) {
		
	}
	
	public void rent(Player player) {
		
	}
	
	public void define(Player player, String regionId) {
		
	}
	
	public void define(Player player, String regionId, int bottomY, int topY) {
		
	}
	
	public void redefine(Player player, String regionId) {
		
	}
	
	public void redefine(Player player, String regionId, int bottomY, int topY) {
		
	}
	
	public void delete(CommandSender sender, String regionId) {
		
	}
	
	public void sendRegionCount(CommandSender sender, OfflinePlayer owner, World world) {
		
	}
	
	public void sendWorth(CommandSender sender, String regionId) {
		
	}
	
	public void sendWorth(CommandSender sender, int width, int length) {
		
	}
	
	public void sendWorth(Player player, double money) {
		
	}
	
	

}
