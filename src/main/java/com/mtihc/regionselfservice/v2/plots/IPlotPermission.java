package com.mtihc.regionselfservice.v2.plots;


public interface IPlotPermission {

	public enum PlotAction {
		BREAK_ANY_SIGN,
		RENTOUT,
		RENTOUT_ANYREGION,
		RENTOUT_ANYWHERE,
		SELL,
		SELL_ANYREGION,
		SELL_ANYWHERE;
	}
	
	public abstract String getPermission(PlotAction action);
	
}
