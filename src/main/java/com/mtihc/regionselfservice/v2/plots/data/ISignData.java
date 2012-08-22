package com.mtihc.regionselfservice.v2.plots.data;

import org.bukkit.block.BlockFace;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.BlockVector;

public interface ISignData extends ConfigurationSerializable {

	public abstract BlockVector getBlockVector();

	public abstract BlockFace getAttachedFace();

	public abstract SignType getSignType();

}