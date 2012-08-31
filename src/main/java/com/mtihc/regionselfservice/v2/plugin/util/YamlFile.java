package com.mtihc.regionselfservice.v2.plugin.util;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class YamlFile {

	private JavaPlugin plugin;
	private YamlConfiguration config = null;
	private File file;

	public YamlFile(JavaPlugin plugin, String filePath) {
		this(plugin, new File(filePath));
	}
	
	/**
	 * Constructor
	 * @param plugin The plugin
	 * @param name the filename without .yml extension
	 */
	public YamlFile(JavaPlugin plugin, File file) {
		if (plugin == null) {
			throw new NullPointerException("Parameter plugin must be non-null.");
		}
		if (file == null) {
			throw new NullPointerException("Parameter file must be non-null.");
		}
		this.plugin = plugin;
		this.file = file;
	}
	
	/**
	 * Returns the name of the file
	 * @return The name of the file
	 */
	public String getName() { return file.getName(); }

	/**
	 * Returns the currently loaded configuration
	 * @return currently loaded config
	 */
	public YamlConfiguration getConfig() {
		return config;
	}

	/**
	 * Loads the yml file
	 */
	public void reload() {
		try {
			config = YamlConfiguration.loadConfiguration(file);
			setDefaults(file.getCanonicalPath().replace(plugin.getDataFolder().getCanonicalPath(), ""));
		}
		catch(Exception e) {
			plugin.getLogger().log(Level.WARNING, plugin.getDescription().getFullName() + " could not load file: " + file + " ", e);
		}
	}
	
	public void setDefaults(String filePath) {
		
		String path = filePath.endsWith(".yml") ? filePath : filePath + ".yml";
		if(path.startsWith(File.separator)) {
			path = path.substring(File.separator.length());
		}
		Bukkit.getLogger().info("setting defaults: " + path);
		InputStream defConfigStream = plugin.getResource(file.getName());
		
		if (defConfigStream != null) {
			Bukkit.getLogger().info("resource found: " + path);
			YamlConfiguration defConfig = YamlConfiguration
				.loadConfiguration(defConfigStream);
			config.options().copyDefaults(true);
			config.setDefaults(defConfig);
			save();
		}
	}

	/**
	 * Saves the yml file
	 */
	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE,
					plugin.getDescription().getFullName() + " could not save to file: " + file + " ", e);
		}
	}

	/**
	 * @return the plugin
	 */
	public JavaPlugin getPlugin() {
		return plugin;
	}
}
