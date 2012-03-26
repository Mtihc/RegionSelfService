package com.mtihc.minecraft.regionselfservice.control;

import com.mtihc.minecraft.regionselfservice.configuration.ConfigYaml;
import com.mtihc.minecraft.regionselfservice.configuration.SignsRentYaml;
import com.mtihc.minecraft.regionselfservice.configuration.SignsSaleYaml;

public class ConfigControl {

	private ConfigYaml config;
	private SignsSaleYaml signsSale;
	private SignsRentYaml signsRent;

	public ConfigControl(ConfigYaml config, SignsSaleYaml signsSale, SignsRentYaml signsRent) {
		this.config = config;
		this.signsSale = signsSale;
		this.signsRent = signsRent;
	}

	public ConfigYaml settings() { return config; }
	public SignsSaleYaml signsSale() { return signsSale; }
	public SignsRentYaml signsRent() { return signsRent; }
	
	
}
