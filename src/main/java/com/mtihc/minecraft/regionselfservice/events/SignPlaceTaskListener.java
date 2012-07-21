package com.mtihc.minecraft.regionselfservice.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.mtihc.minecraft.regionselfservice.RegionSelfServicePlugin;

public class SignPlaceTaskListener implements Listener {

	private final Map<String, SignPlaceTask> tasks;
	
	public SignPlaceTaskListener() {
		tasks = new HashMap<String, SignPlaceTask>();
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onSignTask(SignChangeEvent event) {
		if(event.isCancelled()) {
			return;
		}
		SignPlaceTask task = tasks.get(event.getPlayer().getName());
		if(task != null) {
			task.cancel();
			int n = event.getLines().length;
			for (int i = 0; i < n; i++) {
				if(task.lines[i] == null) {
					event.setLine(i, "");
				}
				else {
					event.setLine(i, task.lines[i]);
				}
			}
		}
	}
	
	public void addTask(String player, String[] lines) {
		new SignPlaceTask(player, lines).schedule();
	}
	
	private class SignPlaceTask implements Runnable {
		
		private RegionSelfServicePlugin plugin;
		private String player;
		private String[] lines;
		private int taskId = -1;

		public SignPlaceTask(String player, String[] lines) {
			this.plugin = RegionSelfServicePlugin.getPlugin();
			this.player = player;
			this.lines = lines;
		}
		
		private void schedule() {
			if(taskId != -1) {
				return;
			}
			taskId = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, this, 600L);
			add();
		}
		
		private void cancel() {
			if(taskId != -1) {
				plugin.getServer().getScheduler().cancelTask(taskId);
				taskId = -1;
			}
			remove();
		}
		
		private void add() {
			SignPlaceTaskListener.this.tasks.put(player, this);
		}
		
		private void remove() {
			SignPlaceTaskListener.this.tasks.remove(player);
		}
		
		@Override
		public void run() {
			cancel();
		}
		
	}
}
