package com.mtihc.regionselfservice.v2.plugin;

import java.util.HashMap;
import java.util.Map;

import com.mtihc.regionselfservice.v2.plots.IPlotPermission;

public class PlotPermissions implements IPlotPermission {

	private Map<PlotAction, String> perms = new HashMap<PlotAction, String>();
	
	public PlotPermissions() {
		perms.put(PlotAction.BREAK_ANY_SIGN, "selfservice.break-any-sign");
		perms.put(PlotAction.RENTOUT, "selfservice.rentout");
		perms.put(PlotAction.RENTOUT_ANYREGION, "selfservice.rentout.anyregion");
		perms.put(PlotAction.RENTOUT_ANYWHERE, "selfservice.rentout.anywhere");
		perms.put(PlotAction.RENTOUT_FOR_FREE, "selfservice.rentout.forfree");
		perms.put(PlotAction.SELL, "selfservice.sell");
		perms.put(PlotAction.SELL_ANYREGION, "selfservice.sell.anyregion");
		perms.put(PlotAction.SELL_ANYWHERE, "selfservice.sell.anywhere");
		perms.put(PlotAction.SELL_FOR_FREE, "selfservice.sell.forfree");
	}

	@Override
	public String getPermission(PlotAction action) {
		return perms.get(action);
	}

}
