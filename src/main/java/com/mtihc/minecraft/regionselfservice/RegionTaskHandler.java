package com.mtihc.minecraft.regionselfservice;

import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.regionselfservice.tasks.AcceptableTask;

public interface RegionTaskHandler {
	void taskRequest(CommandSender sender, AcceptableTask task);
	void taskAccept(CommandSender sender);
}
