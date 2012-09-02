package com.mtihc.regionselfservice.v2.plots.signs;

import java.util.List;

import org.bukkit.block.Sign;
import org.bukkit.util.BlockVector;

import com.mtihc.regionselfservice.v2.plots.IPlotSignData;
import com.mtihc.regionselfservice.v2.plots.Plot;
import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;


public class ForRent extends PlotSignType<ForRentSign> {

	ForRent() {
		super("FOR_RENT", new String[]{"For Rent"});
	}

	@Override
	public ForRentSign createPlotSign(Plot plot, IPlotSignData data) {
		if(data instanceof ForRentSign) {
			return (ForRentSign) data;
		}
		if(data.getType() != this) {
			throw new IllegalArgumentException("Parameter data is not of the correct " + PlotSignType.class.getName() + ". Expected " + PlotSignType.FOR_RENT.name() + ".");
		}
		return new ForRentSign(plot, (ForRentSignData) data);
	}

	@Override
	public ForRentSign createPlotSign(Plot plot, Sign sign, String[] lines) throws SignException {
		
		String firstLine = lines[0];
		List<String> options = getFirstLineOptions();
		boolean valid = false;
		for (String string : options) {
			if(string.equalsIgnoreCase(firstLine)) {
				valid = true;
				break;
			}
		}
		
		if(!valid) {
			throw new SignException("The sign's 1st line should be \"" + getFirstLineOptions().get(0) + "\" if you want to rent out a region.");
		}
		
		BlockVector coords = sign.getLocation().toVector().toBlockVector();
		
		return createPlotSign(plot, new ForRentSignData(this, coords));
	}

}
