package com.mtihc.regionselfservice.v2.plots;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.util.BlockVector;

import com.mtihc.regionselfservice.v2.plots.data.SignDataForRent;

@DelegateDeserialization(SignDataForRent.class)
public class SignForRent extends SignDataForRent implements ISign {

	private Plot plot;
	private World world;

	public SignForRent(Plot plot, BlockVector coords, 
			BlockFace attachedFace, double costPerHour) {
		this(plot, new SignDataForRent(coords, attachedFace, costPerHour));
	}
	
	public SignForRent(Plot plot, SignDataForRent data) {
		super(data);
		this.plot = plot;
		this.world = plot.getWorld();
	}

	@Override
	public Plot getPlot() {
		return plot;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public Location getLocation() {
		return getBlockVector().toLocation(world);
	}

	@Override
	public Sign getSign() {
		Block block = getLocation().getBlock();
		if(block.getState() instanceof Sign) {
			return (Sign) block.getState();
		}
		else {
			return null;
		}
	}

	
}
