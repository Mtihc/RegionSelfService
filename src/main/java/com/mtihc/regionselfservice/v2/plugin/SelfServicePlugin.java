package com.mtihc.regionselfservice.v2.plugin;

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
import com.mtihc.regionselfservice.v2.plots.IPlotPermission;
import com.mtihc.regionselfservice.v2.plots.ISignValidator;
import com.mtihc.regionselfservice.v2.plots.PlotManager;
import com.mtihc.regionselfservice.v2.plugin.util.commands.CommandException;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class SelfServicePlugin extends JavaPlugin {
	
	
	private PlotManager manager;
	private PlotCommand cmd;
	private PlotManagerConfig config;
	
	public PlotManager getManager() {
		return manager;
	}
	
	
	
	
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	@Override
	public void onEnable() {
		WorldGuardPlugin worldGuard = setupWorldGuard();
		if(worldGuard == null) {
			getLogger().log(Level.SEVERE, "Couldn't find WorldGuard plugin.");
			
			setEnabled(false);
			return;
		}
		
		Economy vault = setupEconomy();
		if(vault == null) {
			getLogger().log(Level.SEVERE, "Couldn't find Vault plugin.");
			
			setEnabled(false);
			return;
		}
		
		
		IEconomy economy = new EconomyVault(vault, getLogger());
		
		this.config = new PlotManagerConfig(this, "config");
		config.reload();
		
		ISignValidator signValidator = new SignValidator(
				config.getFirstLineForRent(), 
				config.getFirstLineForSale());
		
		IPlotPermission perms = new PlotPermissions();
		
		this.manager = new SelfServiceManager(this, worldGuard, economy, config, signValidator, perms);
		
		this.cmd = new PlotCommand(manager, null, new String[]{"plot", "ss", "selfservice"});
	}
	
	
	
	
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		String lbl = label.toLowerCase();
		if(cmd.getLabel().equals(lbl) || cmd.getAliases().contains(lbl)) {
			
			try {
				// try to execute command
				cmd.execute(sender, args);
			} catch (CommandException e) {
				
				// send error messages
				Throwable error = e;
				while(error != null && error instanceof CommandException) {
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
		}
		else {
			// we don't know that command label
			return false;
		}
	}
	
	
	
	
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#getConfig()
	 */
	@Override
	public FileConfiguration getConfig() {
		return config.getConfig();
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#reloadConfig()
	 */
	@Override
	public void reloadConfig() {
		config.reload();
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#saveConfig()
	 */
	@Override
	public void saveConfig() {
		config.save();
	}
	
	
	
	
	
	private WorldGuardPlugin setupWorldGuard() {
		Plugin worldGuardPlugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(worldGuardPlugin == null || !(worldGuardPlugin instanceof WorldGuardPlugin)) {
			return null;
		}
		else {
			return (WorldGuardPlugin) worldGuardPlugin;
		}
	}
	
	private Economy setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return null;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return null;
        }
        Economy econ = rsp.getProvider();
        if(econ == null) {
        	return null;
        }
        return econ;
    }
}
