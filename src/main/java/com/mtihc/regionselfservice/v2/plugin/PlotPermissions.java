package com.mtihc.regionselfservice.v2.plugin;

import java.util.HashMap;
import java.util.Map;

import com.mtihc.regionselfservice.v2.plots.IPlotPermission;

public class PlotPermissions implements IPlotPermission {

	private Map<PlotAction, String> perms = new HashMap<PlotAction, String>();
	
	public PlotPermissions() {
		perms.put(PlotAction.BREAK_ANY_SIGN, Permission.BREAK_ANY_SIGN);
		perms.put(PlotAction.BYPASS_BUY_COST, Permission.BUY_BYPASSCOST);
		perms.put(PlotAction.BYPASS_CREATE_COST, Permission.CREATE_BYPASSCOST);
		perms.put(PlotAction.BYPASS_MAX_REGIONS, Permission.BYPASSMAX_REGIONS);
		perms.put(PlotAction.CREATE_ANYSIZE, Permission.CREATE_ANYSIZE);
		perms.put(PlotAction.CREATE_ANYWHERE, Permission.CREATE_ANYWHERE);
		perms.put(PlotAction.RENTOUT, Permission.RENTOUT);
		perms.put(PlotAction.RENTOUT_ANYREGION, Permission.RENTOUT_ANYREGION);
		perms.put(PlotAction.RENTOUT_ANYWHERE, Permission.RENTOUT_ANYWHERE);
		perms.put(PlotAction.RENTOUT_FOR_FREE, Permission.RENTOUT_FREE);
		perms.put(PlotAction.SELL, Permission.SELL);
		perms.put(PlotAction.SELL_ANYREGION, Permission.SELL_ANYREGION);
		perms.put(PlotAction.SELL_ANYWHERE, Permission.SELL_ANYWHERE);
		perms.put(PlotAction.SELL_FOR_FREE, Permission.SELL_FREE);
	}

	@Override
	public String getPermission(PlotAction action) {
		return perms.get(action);
	}

}
