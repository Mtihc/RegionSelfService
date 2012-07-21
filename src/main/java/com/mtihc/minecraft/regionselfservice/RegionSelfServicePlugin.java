package com.mtihc.minecraft.regionselfservice;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.regionselfservice.configuration.ConfigYaml;
import com.mtihc.minecraft.regionselfservice.configuration.SignsRentYaml;
import com.mtihc.minecraft.regionselfservice.configuration.SignsSaleYaml;
import com.mtihc.minecraft.regionselfservice.control.ConfigControl;
import com.mtihc.minecraft.regionselfservice.control.EconomyControl;
import com.mtihc.minecraft.regionselfservice.control.MessageControl;
import com.mtihc.minecraft.regionselfservice.control.RegionControl;
import com.mtihc.minecraft.regionselfservice.control.RentControl;
import com.mtihc.minecraft.regionselfservice.control.VaultControl;
import com.mtihc.minecraft.regionselfservice.control.WoodenSignControl;
import com.mtihc.minecraft.regionselfservice.events.SignBreakListener;
import com.mtihc.minecraft.regionselfservice.events.SignClickListener;
import com.mtihc.minecraft.regionselfservice.events.SignPlaceListener;
import com.mtihc.minecraft.regionselfservice.events.SignPlaceTaskListener;
import com.mtihc.minecraft.regionselfservice.exceptions.EconomyInstantiateException;
import com.mtihc.minecraft.regionselfservice.tasks.AcceptIsRequired;
import com.mtihc.minecraft.regionselfservice.tasks.AcceptableTask;
import com.mtihc.minecraft.regionselfservice.tasks.AcceptableTaskException;
import com.mtihc.minecraft.regionselfservice.tasks.AcceptableTaskHandler;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionSelfServicePlugin extends JavaPlugin implements RegionTaskHandler {

	private WorldGuardPlugin worldGuard;
	private static RegionSelfServicePlugin plugin;
	private ConfigYaml config;
	private RegionSelfServiceCommand command;

	private AcceptableTaskHandler taskHandler;
	private ConfigControl configControl;
	private WoodenSignControl signControl;
	private RegionControl regionControl;
	private RentControl rentControl;
	private EconomyControl economyControl;
	private MessageControl messageControl;
	private SignPlaceTaskListener signPlaceTaskListener;
	
	public static RegionSelfServicePlugin getPlugin() {
		return plugin;
	}

	public ConfigControl config() { return configControl; }
	
	public WoodenSignControl woodenSigns() { return signControl; }
	
	public RegionControl regions() { return regionControl; }
	
	public EconomyControl economy() { return economyControl; }
	
	public RentControl rental() { return rentControl; }
	
	public MessageControl messages() { return messageControl; }
	
	public static void addSignPlaceTask(String playerName, String[] lines) {
		plugin.signPlaceTaskListener.addTask(playerName, lines);
	}
	
	@Override
	public void onDisable() {
		info("disabled.");
		
	}

	@Override
	public void onEnable() {
		
		// dependencies
		// Vault and WorldGuard
		
		try {
			economyControl = new VaultControl(getServer());
			info("Economy plugin found: ");
			info("    " + getServer().getPluginManager().getPlugin("Vault").getDescription().getFullName() + " (" + economyControl.getName() + ")");
		} catch (EconomyInstantiateException e) {
			severe("Couldn't hook into economy plugin Vault.");
			severe("Do you have Vault and an economy plugin installed?");
			severe("Plugin was not enabled.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if (!setupWorldGuard()) {
			severe("Couldn't find protection plugin: WorldGuard.");
			severe("Plugin was not enabled.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		else {
			info("Protection plugin found: ");
			info("    " + worldGuard.getDescription().getFullName());
		}
		
		
		taskHandler = new AcceptableTaskHandler(this);
		
		// yaml files
		config = new ConfigYaml(this);
		config.reload();
		
		SignsRentYaml rentsigns = new SignsRentYaml(this);
		rentsigns.reload();
		
		SignsSaleYaml salesigns = new SignsSaleYaml(this);
		salesigns.reload();
		
		
		// command stuff
		configControl = new ConfigControl(config, salesigns, rentsigns);
		signControl = new WoodenSignControl(configControl);
		regionControl = new RegionControl(worldGuard);
		rentControl = new RentControl(getServer(), worldGuard, configControl);
		messageControl = new MessageControl(economyControl);

		plugin = this;
		
		command = new RegionSelfServiceCommand(this);
		
		
		// event listeners
		SignPlaceListener placeListener = new SignPlaceListener();
		getServer().getPluginManager().registerEvents(placeListener, this);
		
		SignBreakListener breakListener = new SignBreakListener();
		getServer().getPluginManager().registerEvents(breakListener, this);
		
		SignClickListener clickListener = new SignClickListener();
		getServer().getPluginManager().registerEvents(clickListener, this);
		
		signPlaceTaskListener = new SignPlaceTaskListener();
		getServer().getPluginManager().registerEvents(signPlaceTaskListener, this);
		
		// enabled message
		info(getDescription().getName() + " enabled!");
	}

	public void info(Object message) {
		getLogger().info(message.toString());
	}
	
	public void severe(Object message) {
		getLogger().severe(message.toString());
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String label, String[] args) {

		if(command.getLabel().equalsIgnoreCase(label) || command.getAliases().contains(label.toLowerCase())) {
			return command.execute(sender, label, args);
		}
		else {
			return false;
		}
	}

	/**
	 * Looks for the WorldGuard plugin in the plugin manager and returns it
	 * 
	 * @return Whether the WorldGuardPlugin object was found
	 */
	private boolean setupWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin != null && plugin instanceof WorldGuardPlugin) {
			worldGuard = (WorldGuardPlugin) plugin;
			return true;
		} else {
			worldGuard = null;
			return false;
		}
	}

	public EconomyControl getEconomy() {
		return economyControl;
	}
	
	public WorldGuardPlugin getWorldGuard() {
		return worldGuard;
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




	@Override
	public void taskRequest(CommandSender sender,
			AcceptableTask task) {
		try {
			taskHandler.run(task);
			// if AcceptIsRequired is not thrown
			// run accept behaviour now!
			doTaskAccept(sender, task);
		} catch (AcceptableTaskException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return;
		} catch (AcceptIsRequired e) {
			return;
		}
	}






	@Override
	public void taskAccept(CommandSender sender) {
		AcceptableTask task;
		try {
			task = taskHandler.accept(sender.getName());
		} catch (AcceptableTaskException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return;
		}
		doTaskAccept(sender, task);
	}
	
	private void doTaskAccept(CommandSender sender, AcceptableTask task) {
		if(task instanceof RegionTaskRedefine) {
			redefineRegionTaskAccept(sender, (RegionTaskRedefine) task);
		}
		else if(task instanceof RegionTaskDefine) {
			defineRegionTaskAccept(sender, (RegionTaskDefine) task);
		}
		else if(task instanceof RegionTaskDelete) {
			deleteRegionTaskAccept(sender, (RegionTaskDelete) task);
		}
	}
	
	private void defineRegionTaskAccept(CommandSender sender, RegionTaskDefine task) {
		if(task.paymentRequired()) {
			sender.sendMessage(ChatColor.GREEN + "You payed " + ChatColor.WHITE + economy().format(task.getCost()));
		}
		
		sendRegionInfo(sender, task.getRegion(), config.getBlockWorth());
	}
	
	private void redefineRegionTaskAccept(CommandSender sender, RegionTaskRedefine task) {
		ProtectedRegion region = task.getRegion();
		ProtectedRegion existing = task.getExisting();
		int oldWidth = Math.abs(existing.getMaximumPoint().getBlockX() - existing.getMinimumPoint().getBlockX()) + 1;
		int oldLength = Math.abs(existing.getMaximumPoint().getBlockZ() - existing.getMinimumPoint().getBlockZ()) + 1;
		int oldHeight = Math.abs(existing.getMaximumPoint().getBlockY() - existing.getMinimumPoint().getBlockY()) + 1;
		int newWidth = Math.abs(region.getMaximumPoint().getBlockX() - region.getMinimumPoint().getBlockX()) + 1;
		int newLength = Math.abs(region.getMaximumPoint().getBlockZ() - region.getMinimumPoint().getBlockZ()) + 1;
		int newHeight = Math.abs(region.getMaximumPoint().getBlockY() - region.getMinimumPoint().getBlockY()) + 1;
		double blockWorth = config.getBlockWorth();
		double oldWorth = getRegionWorth(oldWidth, oldLength, blockWorth);
		double newWorth = getRegionWorth(newWidth, newLength, blockWorth);
		messageControl.resized(sender, task.getRegion().getOwners().getPlayers(), task.getRegion().getMembers().getPlayers(), task.getRegion().getId(), oldWorth, newWorth, oldWidth, oldLength, oldHeight, newWidth, newLength, newHeight);
	}
	
	private void deleteRegionTaskAccept(CommandSender sender, RegionTaskDelete task) {
		messageControl.removed(sender, task.getOwners(), task.getMembers(), task.getRegionId(), task.getWorth());
	}
	
	

	public double getRegionWorth(ProtectedRegion region, double blockWorth) {
		return getRegionWorth(region.getMinimumPoint(), region.getMaximumPoint(), blockWorth);
	}
	
	public double getRegionWorth(BlockVector minPoint, BlockVector maxPoint, double blockWorth) {
		int width = Math.abs(maxPoint.getBlockX() - minPoint.getBlockX()) + 1;
		int length = Math.abs(maxPoint.getBlockZ() - minPoint.getBlockZ()) + 1;
		return getRegionWorth(width, length, blockWorth);
	}
	
	public double getRegionWorth(int width, int length, double blockWorth) {
		return width * length * blockWorth;
	}
	
	public String toUserFriendlyString(Set<String> playerNames) {
		String result = "";
		for (String string : playerNames) {
			result += ", " + string;
		}
		if(!result.isEmpty()) {
			result = result.substring(2);
		}
		else {
			result = "nobody";
		}
		return result;
	}
	
	
	
	
	
	
	
	
	

	//------------------------------
	//
	// Explain worth and size
	//
	//------------------------------
	
	
	public void explainRegionWorth(CommandSender sender, double blockWorth, String regionName, World world) {
		RegionManager mgr = worldGuard.getRegionManager(world);
		ProtectedRegion region = mgr.getRegion(regionName);
		if(region == null) {
			sender.sendMessage(ChatColor.RED + "Region '" + regionName + "' doesn't exist in world '" + world.getName() + "'.");
			return;
		}
		
		int width = Math.abs(region.getMaximumPoint().getBlockX() - region.getMinimumPoint().getBlockX()) + 1;
		int length = Math.abs(region.getMaximumPoint().getBlockZ() - region.getMinimumPoint().getBlockZ()) + 1;
		sender.sendMessage(ChatColor.GREEN + "Region " + ChatColor.WHITE + region.getId() + ChatColor.GREEN + " with a size of "
				+ ChatColor.WHITE + String.valueOf(width) + "x"
				+ String.valueOf(length) + ChatColor.GREEN + " blocks, ");
		double cost = plugin.getRegionWorth(width, length, blockWorth);
		sender.sendMessage(ChatColor.GREEN + "is worth about "
				+ ChatColor.WHITE + economyControl.format(cost)
				+ ChatColor.GREEN + ", based on the region's size.");
	}

	public void explainRegionWorth(CommandSender sender, double blockWorth, int width, int length) {
		double cost = plugin.getRegionWorth(width, length, blockWorth);
		sender.sendMessage(ChatColor.GREEN + "For a region with a size of "
				+ ChatColor.WHITE + String.valueOf(width) + "x"
				+ String.valueOf(length) + ChatColor.GREEN + " blocks, ");
		sender.sendMessage(ChatColor.GREEN + "you would pay about "
				+ ChatColor.WHITE + economyControl.format(cost)
				+ ChatColor.GREEN + ".");
	}

	public void explainRegionSize(CommandSender sender, double blockWorth, double money) {
		int size = regionControl.getRegionSize(money, blockWorth);
		sender.sendMessage(ChatColor.GREEN + "For " + ChatColor.WHITE
				+ economyControl.format(money) + ChatColor.GREEN + ", ");
		sender.sendMessage(ChatColor.GREEN
				+ "you can get a region with a size of about "
				+ ChatColor.WHITE + String.valueOf(size) + "x"
				+ String.valueOf(size) + ChatColor.GREEN + " blocks.");
	}
	


	//------------------------------
	//
	// Send region info
	//
	//------------------------------
	
	public void sendRegionInfo(CommandSender sender, ProtectedRegion region, double blockWorth) {
		regionControl.sendRegionInfo(sender, region);
		// Worth
		sender.sendMessage(ChatColor.YELLOW + "Worth: " + ChatColor.WHITE + String.valueOf(plugin.getRegionWorth(region, blockWorth)) + ChatColor.YELLOW + " (based on size)");
	}

	public void sendRegionInfo(CommandSender sender, ProtectedRegion region, double blockWorth, double cost, boolean isRentCost) {
		sendRegionInfo(sender, region, blockWorth);
		// Cost
		String costString = economyControl.format(cost);
		if(isRentCost) {
			costString += " per hour";
		}
		
		String taxString = "";
		if(cost >= config.getTaxFromPrice()) {
			taxString = ChatColor.YELLOW + "(Tax: " + ChatColor.WHITE + getEconomy().format(cost * config.getTaxPercent() / 100) + ChatColor.YELLOW + ")";
		}
		else {
			taxString = ChatColor.YELLOW + "(Tax starts at: " + ChatColor.WHITE + getEconomy().format(config.getTaxFromPrice()) + ChatColor.YELLOW + ")";
		}
		sender.sendMessage(ChatColor.YELLOW + "Cost: " + ChatColor.WHITE + costString + taxString);
		String tab = "  ";
		double balance = economyControl.getBalance(sender.getName());
		if(isRentCost) {
			int hours = (int)(balance / cost);
			ChatColor color;
			if(hours == 0) {
				color = ChatColor.RED;
				sender.sendMessage(tab + color + "You don't have enough to rent this region. You still require " + ChatColor.WHITE + economyControl.format(cost - balance) + color + " to rent this region for 1 hour.");
			}
			else {
				color = ChatColor.GREEN;
				sender.sendMessage(tab + color + "You are able to rent this region for " + ChatColor.WHITE + hours + " hours" + color + ". It would cost you " + ChatColor.WHITE + economyControl.format(hours * cost) + color + ".");
				sender.sendMessage(tab + color + "Use the rent command: " + ChatColor.WHITE + "/selfservice rent [hours]");
			}
			
		}
		else {
			ChatColor color;
			if(balance < cost) {
				color = ChatColor.RED;
				sender.sendMessage(tab + color + "You can't afford this region. You still require " + ChatColor.WHITE + economyControl.format(cost - balance) + color + ".");
			}
			else {
				color = ChatColor.GREEN;
				sender.sendMessage(tab + color + "You can afford this region. You'd have " + ChatColor.WHITE + economyControl.format(balance - cost) + color + " left.");
				sender.sendMessage(tab + color + "Use the buy command: " + ChatColor.WHITE + "/selfservice buy");
			}
		}
	}
}
