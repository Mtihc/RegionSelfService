package com.mtihc.minecraft.regionselfservice.tasks;

import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.regionselfservice.tasks.AcceptableTask.AcceptResult;

public class AcceptableTaskHandler {

	private JavaPlugin plugin;
	private HashMap<String, IgnoreTask> tasks;
	
	public AcceptableTaskHandler(JavaPlugin plugin) {
		this.plugin = plugin;
		this.tasks = new HashMap<String, IgnoreTask>();
	}

	public void run(AcceptableTask task) throws AcceptIsRequired, AcceptableTaskException {
		if(!task.acceptIsRequired()) {
			task.run(AcceptResult.NOT_REQUIRED);
		}
		else {
			IgnoreTask innerTask = new IgnoreTask(task);
			
			innerTask.taskSchedule();
			tasks.put(task.getPlayerName(), innerTask);
			
			throw new AcceptIsRequired();
		}
	}
	
	public AcceptableTask accept(String playerName) throws AcceptableTaskException {
		IgnoreTask innerTask = tasks.remove(playerName);
		if(innerTask == null) {
			throw new AcceptableTaskException("There are no tasks to accept for player " + playerName);
		}
		
		innerTask.taskCancel();
		
		innerTask.task.run(AcceptResult.ACCEPTED);
		return innerTask.task;
	}
	
	public AcceptableTask deny(String playerName) throws AcceptableTaskException {
		IgnoreTask innerTask = tasks.remove(playerName);
		if(innerTask == null) {
			throw new AcceptableTaskException("There are no tasks to accept for player " + playerName);
		}
		
		innerTask.taskCancel();
		
		innerTask.task.run(AcceptResult.DENIED);
		return innerTask.task;
	}
	
	
	private class IgnoreTask implements Runnable {
		
		private AcceptableTask task;
		private int taskId;

		private IgnoreTask(AcceptableTask task) {
			this.task = task;
		}
		
		private void taskSchedule() {
			taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, task.getAcceptTime());
		}
		
		private void taskCancel() {
			plugin.getServer().getScheduler().cancelTask(taskId);
		}

		@Override
		public void run() {
			tasks.remove(task.getPlayerName());
			try {
				task.run(AcceptResult.IGNORED);
			} catch (AcceptableTaskException e) {
			}
		}
	}
}
