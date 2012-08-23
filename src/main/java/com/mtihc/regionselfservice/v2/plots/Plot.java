package com.mtihc.regionselfservice.v2.plots;

import java.util.Collection;

import org.bukkit.configuration.serialization.DelegateDeserialization;

import com.mtihc.regionselfservice.v2.plots.data.ISignData;
import com.mtihc.regionselfservice.v2.plots.data.PlotData;
import com.mtihc.regionselfservice.v2.plots.data.SignDataForRent;
import com.mtihc.regionselfservice.v2.plots.data.SignDataForSale;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

@DelegateDeserialization(PlotData.class)
public class Plot extends PlotData {

	private PlotWorld plotWorld;

	public Plot(PlotWorld plotWorld, String regionId) {
		super(plotWorld.getWorld(), regionId);
		this.plotWorld = plotWorld;
	}
	
	public Plot(PlotWorld plotWorld, PlotData data) {
		super(data);
		Collection<ISignData> signValues = signs.values();
		for (ISignData signData : signValues) {
			ISign sign = createPlotSign(signData);
			signs.put(sign.getBlockVector(), sign);
		}
	}
	
	public void save() {
		plotWorld.plots.set(getRegionId(), this);
	}
	
	public void delete() {
		plotWorld.plots.remove(regionId);
	}
	
	public PlotManager getPlotManager() {
		return plotWorld.getPlotManager();
	}
	
	public PlotWorld getPlotWorld() {
		return plotWorld;
	}
	
	public ProtectedRegion getRegion() {
		return plotWorld.getRegionManager().getRegion(getRegionId());
	}
	
	public double getWorth() {
		double blockWorth = plotWorld.getConfig().getBlockWorth();
		return getWorth(blockWorth);
	}
	
	public double getWorth(double blockWorth) {
		ProtectedRegion region = getRegion();
		return PlotControl.getWorth(region, blockWorth);
	}

	protected ISign createPlotSign(ISignData data) {
		if(data instanceof SignDataForSale) {
			return new SignForSale(this, (SignDataForSale) data);
		}
		else if(data instanceof SignDataForRent) {
			return new SignForRent(this, (SignDataForRent) data);
		}
		else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.mtihc.regionselfservice.v2.plots.data.PlotData#setSign(com.mtihc.regionselfservice.v2.plots.data.ISignData)
	 */
	@Override
	public void setSign(ISignData sign) {
		if(!(sign instanceof ISign)) {
			sign = createPlotSign(sign);
		}
		super.setSign(sign);
	}
	
}
