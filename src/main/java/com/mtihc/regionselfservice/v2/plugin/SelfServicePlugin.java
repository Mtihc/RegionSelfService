package com.mtihc.regionselfservice.v2.plugin;

import java.io.File;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.regionselfservice.v2.plots.IEconomy;
import com.mtihc.regionselfservice.v2.plots.PlotManager;
import com.mtihc.regionselfservice.v2.plugin.util.commands.CommandException;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;


public class SelfServicePlugin extends JavaPlugin {
    
    private PlotManager manager;
    private PlotCommand cmd;
    private PlotManagerConfig config;
    
    public PlotManager getManager() {
	return this.manager;
    }
    
    @Override
    public void onEnable() {
	WorldGuardPlugin worldGuard = setupWorldGuard();
	IEconomy economy = setupEconomy();
	
	if (worldGuard == null || economy == null) {
	    getServer().getPluginManager().disablePlugin(this);
	    return;
	}
	
	this.config = new PlotManagerConfig(this, getDataFolder() + File.separator + "config.yml");
	
	this.manager = new SelfServiceManager(this, worldGuard, economy, this.config);
	
	reloadConfig();
	
	this.cmd = new PlotCommand(this.manager, null, new String[] {"plot", "ss", "selfservice"});
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	
	String lbl = label.toLowerCase();
	if (this.cmd.getLabel().equals(lbl) || this.cmd.getAliases().contains(lbl)) {
	    
	    try {
		// try to execute command
		this.cmd.execute(sender, args);
	    } catch (CommandException e) {
		
		// send error messages
		Throwable error = e;
		while (error != null && error instanceof CommandException) {
		    sender.sendMessage(ChatColor.RED + error.getMessage());
		    error = error.getCause();
		}
		
		// show what the player typed
		String echo = "/" + label;
		for (int i = 0; i < args.length; i++) {
		    echo += " " + args[i];
		}
		sender.sendMessage(echo);
	    }
	    return true;
	} else {
	    // we don't know that command label
	    return false;
	}
    }
    
    @Override
    public FileConfiguration getConfig() {
	return this.config.getConfig();
    }
    
    @Override
    public void reloadConfig() {
	this.config.reload();
	
	((PlotWorldConfig) this.manager.getDefaultWorldConfig()).reload();
	this.manager.reloadWorlds();
    }
    
    @Override
    public void saveConfig() {
	this.config.save();
    }
    
    private WorldGuardPlugin setupWorldGuard() {
	Plugin worldGuardPlugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
	if (worldGuardPlugin == null || !(worldGuardPlugin instanceof WorldGuardPlugin)) {
	    getLogger().log(Level.SEVERE, ChatColor.RED + " Couldn't find WorldGuard plugin. Please install WorldEdit and WorldGuard.");
	    return null;
	} else {
	    getLogger().log(Level.INFO, " Protection plugin found: ");
	    getLogger().log(Level.INFO, "    " + worldGuardPlugin.getDescription().getFullName());
	    return (WorldGuardPlugin) worldGuardPlugin;
	}
    }
    
    private IEconomy setupEconomy() {
	Plugin vault = getServer().getPluginManager().getPlugin("Vault");
	RegisteredServiceProvider<Economy> rsp = null;
	Economy econ = null;
	
	if (vault != null) {
	    rsp = getServer().getServicesManager().getRegistration(Economy.class);
	    if (rsp != null) {
		econ = rsp.getProvider();
	    }
	}
	
	if (rsp == null) {
	    getLogger().log(Level.SEVERE, ChatColor.RED + " Couldn't find Vault plugin. Please install Vault and an economy plugin.");
	    return null;
	} else if (econ == null) {
	    getLogger().log(Level.SEVERE, ChatColor.RED + " Vault couldn't find an economy plugin. Please install an economy plugin that is supported by Vault.");
	    return null;
	} else {
	    getLogger().log(Level.INFO, " Economy plugin found: ");
	    getLogger().log(Level.INFO, "    " + vault.getDescription().getFullName() + " (" + econ.getName() + ")");
	    return new EconomyVault(econ, getLogger());
	}
	
    }
}
