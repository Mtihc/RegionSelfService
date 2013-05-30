package com.mtihc.regionselfservice.v2.plugin.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

public abstract class Repository<K, V> {

	protected File directory;
	protected Logger logger;
	
	public Repository(String directory) {
		this(new File(directory), null);
	}

	public Repository(File directory) {
		this(directory, null);
	}
	
	public Repository(String directory, Logger logger) {
		this(new File(directory), logger);
	}
	
	public Repository(File directory, Logger logger) {
		this.directory = directory;
		this.directory.mkdirs();
		this.logger = logger;
	}
	
	protected String getValueTypeName() {
		return getClass().getTypeParameters()[0].getGenericDeclaration().getSimpleName();
	}
	
	public V get(K key) {
		File file = new File(getPathByKey(key));
		return load(file);
	}
	
	@SuppressWarnings("unchecked")
	protected V load(File file) {
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(file);
		} catch (FileNotFoundException e) {
			return null;
		} catch (Exception e) {
			if(logger != null) logger.log(Level.WARNING, "Failed to load " + getValueTypeName() + ". ", e);
		}
		try {
			return (V) config.get("data");
		} catch(ClassCastException e) {
			return null;
		}
	}
	
	protected void save(File file, V value) {
		YamlConfiguration config = new YamlConfiguration();
		config.set("data", value);
		try {
			config.save(file);
		} catch (IOException e) {
			if(logger != null) logger.log(Level.WARNING, "Failed to save " + getValueTypeName() + ".", e);
		}
	}
	
	protected abstract String getPathByKey(K key);
	
	public void set(K key, V value) {
		if(value == null) {
			remove(key);
			return;
		}
		File file = new File(getPathByKey(key));
		save(file, value);
	}
	
	public boolean has(K key) {
		return new File(getPathByKey(key)).exists();
	}
	
	public void remove(K key) {
		File file = new File(getPathByKey(key));
		deleteDirectory(file);
	}
	
	
	protected static boolean deleteDirectory(File dir) {
		boolean success = true;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                success = deleteDirectory(new File(dir, children[i]));
            }
        }
    
        if(success) {
            // The directory is now empty so delete it
        	return dir.delete();
        }
        else {
        	return false;
        }
    }
}
