package com.mtihc.minecraft.regionselfservice.control;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.regionselfservice.RentSession;
import com.mtihc.minecraft.regionselfservice.RentSessionObserver;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RentControl implements RentSessionObserver {

	private ConfigControl configControl;
	private List<RentSession> sessions;
	private Server server;
	private WorldGuardPlugin worldGuard;

	public RentControl(Server server, WorldGuardPlugin worldGuard, ConfigControl configControl) {

		this.server = server;
		this.worldGuard = worldGuard;
		this.configControl = configControl;
		// start all rent sessions
		this.sessions = configControl.signsRent().getRentSessions();
		if (this.sessions != null) {
			for (RentSession session : this.sessions) {
				session.setObserver(this);
				session.start();
			}
		}
		else {
			this.sessions = new ArrayList<RentSession>();
		}
	}

	
	@Override
	public void onHourPassed(RentSession session) {
		if (session.getHoursRemaining() < 1) {
			// end
			endRentSession(session);
		} else {
			// next hour
			configControl.signsRent().setMemberRemainingHours(session.getWorld(),
					session.getRegion(), session.getPlayerName(),
					session.getHoursRemaining());
			configControl.signsRent().save();
		}
	}

	public void startRentSession(RentSession session) {
		session.setObserver(this);
		session.start();
		if (!sessions.contains(session)) {
			sessions.add(session);
		}
		configControl.signsRent().setRentSession(session);
		configControl.signsRent().save();
	}

	private void stopRentSession(RentSession session) {
		session.stop();
	}

	private void endRentSession(RentSession session) {
		stopRentSession(session);
	
		World world = server.getWorld(session.getWorld());
		RegionManager mgr = worldGuard.getRegionManager(world);
		ProtectedRegion region = mgr.getRegion(session.getRegion());
		if (region == null) {
			return;
		}
		region.getMembers().removePlayer(session.getPlayerName());
		mgr.addRegion(region);
		try {
			mgr.save();
		} catch (ProtectionDatabaseException e) {
			Player sender = server.getPlayerExact(session.getPlayerName());
			if(sender != null) {
				sender.sendMessage(ChatColor.RED + "Unable to save changes to region '" + session.getRegion() + "'.");
				sender.sendMessage(ChatColor.RED + "[WorldGuard] " + e.getMessage());
				e.printStackTrace();
				return;
			}
		}
		configControl.signsRent().clearMember(session.getWorld(), session.getRegion(),
				session.getPlayerName());
		sessions.remove(session);
		configControl.signsRent().save();
	}
	

}
