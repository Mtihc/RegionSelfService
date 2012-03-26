package com.mtihc.minecraft.regionselfservice.exceptions;

public class EconomyInstantiateException extends Exception {

	private static final long serialVersionUID = 377832920500144950L;
	
	private String pluginName;

	public EconomyInstantiateException(String pluginName) {
		this(pluginName, "Could not find economy plugin " + pluginName);
	}

	public EconomyInstantiateException(String pluginName, String message) {
		super(message);
		this.pluginName = pluginName;
	}

	/**
	 * @return the pluginName
	 */
	public String getPluginName() {
		return pluginName;
	}


}
