package com.mtihc.regionselfservice.v2.plugin.util.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface ICommand {

	String getLabel();
	
	List<String> getAliases();
	
	String getArgumentSyntax();
	
	String getDescription();
	
	String[] getHelp();
	
	String getUniqueLabel();
	
	String getUsage();
	
	String getPermission();
	
	void execute(CommandSender sender, String[] args) throws CommandException;
	
	ICommand getParent();
	
	ICommand getNested(String label);
	
	String[] getNestedLabels();
	
	boolean hasNested();
}
