package com.mtihc.regionselfservice.v2.plugin;

import java.io.File;
import java.io.InputStream;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class PlotWorldConfigDefault extends PlotWorldConfig {
    
    private File resourceFile;
    private JavaPlugin plugin;
    
    public PlotWorldConfigDefault(JavaPlugin plugin, String filePath, String resourceFilePath) {
	this(plugin, new File(filePath), new File(resourceFilePath));
    }
    
    public PlotWorldConfigDefault(JavaPlugin plugin, File file, File resourceFile) {
	super(file);
	this.resourceFile = resourceFile;
	this.plugin = plugin;
	reload();
    }
    
    @Override
    public void reload() {
	super.reload();
	InputStream resource = this.plugin.getResource(this.resourceFile.getPath());
	if (resource != null) {
	    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(resource);
	    getConfig().options().copyDefaults(true);
	    getConfig().setDefaults(defConfig);
	    save();
	}
    }
    
}
