package com.mtihc.regionselfservice.v2.plots;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.util.BlockVector;

import com.mtihc.regionselfservice.v2.plots.data.SignDataForSale;

@DelegateDeserialization(SignDataForSale.class)
public class SignForSale extends SignDataForSale implements ISign {

	private Plot plot;
	private World world;

	public SignForSale(Plot plot, BlockVector coords,
			BlockFace attachedFace, double cost) {
		this(plot, new SignDataForSale(coords, attachedFace, cost));
	}
	
	public SignForSale(Plot plot, SignDataForSale data) {
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
