package com.mtihc.regionselfservice.v2.plots;


public interface IPlotPermission {

	public enum PlotAction {
		BREAK_ANY_SIGN,
		BYPASS_BUY_COST,
		BYPASS_CREATE_COST,
		BYPASS_MAX_REGIONS,
		CREATE_ANYSIZE,
		CREATE_ANYWHERE,
		REDEFINE_ANYREGION,
		RENTOUT,
		RENTOUT_ANYREGION,
		RENTOUT_ANYWHERE,
		RENTOUT_FOR_FREE,
		SELL,
		SELL_ANYREGION,
		SELL_ANYWHERE,
		SELL_FOR_FREE;
	}
	
	public abstract String getPermission(PlotAction action);
	
}
