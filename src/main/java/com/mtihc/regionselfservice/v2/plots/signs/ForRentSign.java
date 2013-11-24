package com.mtihc.regionselfservice.v2.plots.signs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.util.BlockVector;

import com.mtihc.regionselfservice.v2.plots.IPlotSign;
import com.mtihc.regionselfservice.v2.plots.Plot;
import com.mtihc.regionselfservice.v2.plots.PlotWorld;

@DelegateDeserialization(ForRentSignData.class)
public class ForRentSign extends ForRentSignData implements IPlotSign {

	private Plot plot;

	public ForRentSign(Plot plot, BlockVector coords) {
		this(plot, new ForRentSignData(coords));
	}
	
	public ForRentSign(Plot plot, ForRentSignData data) {
		super(data);
		this.plot = plot;
	}
	
	@Override
	public Plot getPlot() {
		return plot;
	}

	@Override
	public PlotWorld getPlotWorld() {
		return plot.getPlotWorld();
	}

	@Override
	public Location getLocation() {
		return getBlockVector().toLocation(plot.getWorld());
	}

	@Override
	public Sign getSign() {
		Block block = getBlock();
		if(block.getState() instanceof Sign) {
			return (Sign) block.getState();
		}
		return null;
	}

	@Override
	public Block getBlock() {
		return plot.getWorld().getBlockAt(getLocation());
	}

}
