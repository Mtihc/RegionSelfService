package com.mtihc.regionselfservice.v2.plots;

import org.bukkit.block.Sign;

import com.mtihc.regionselfservice.v2.plots.data.ISignData;
import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;

public interface ISignValidator {

	public abstract boolean isPlotSign(Sign sign);
	
	public abstract ISignData createPlotSign(Sign sign) throws SignException;
	
	
}
