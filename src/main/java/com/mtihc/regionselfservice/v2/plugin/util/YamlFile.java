package com.mtihc.regionselfservice.v2.plugin.util;


import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

public class YamlFile {

	private YamlConfiguration config = null;
	private File file;
	private Logger logger;

	public YamlFile(String filePath) {
		this(new File(filePath), null);
	}
	
	public YamlFile(String filePath, Logger logger) {
		this(new File(filePath), null);
	}
	
	public YamlFile(File file) {
		this(file, null);
	}
	
	/**
	 * Constructor
	 * @param plugin The plugin
	 * @param name the filename without .yml extension
	 */
	public YamlFile(File file, Logger logger) {
		if (file == null) {
			throw new NullPointerException("Parameter file must be non-null.");
		}
		this.file = file;
		this.logger = logger;
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
		}
		catch(Exception e) {
			if(logger != null) {
				logger.log(Level.WARNING, "Could not load file: " + file + " ", e);
			}
		}
	}

	/**
	 * Saves the yml file
	 */
	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			if(logger != null) {
				logger.log(Level.SEVERE,
						"Could not save to file: " + file + " ", e);
			}
		}
	}
}
