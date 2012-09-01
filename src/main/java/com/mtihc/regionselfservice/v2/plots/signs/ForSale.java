package com.mtihc.regionselfservice.v2.plots.signs;

import java.util.List;

import org.bukkit.block.Sign;
import org.bukkit.util.BlockVector;

import com.mtihc.regionselfservice.v2.plots.Plot;
import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;


public class ForSale extends PlotSignType<ForSaleSign> {

	ForSale() {
		super("FOR_SALE", new String[]{"For Sale"});
	}

	@Override
	public ForSaleSign createPlotSign(Plot plot, IPlotSignData data) {
		if(data instanceof ForSaleSign) {
			return (ForSaleSign) data;
		}
		if(data.getType() != this) {
			throw new IllegalArgumentException("Parameter data is not of the correct " + PlotSignType.class.getName() + ". Expected " + PlotSignType.FOR_SALE.name() + ".");
		}
		return new ForSaleSign(plot, (ForSaleSignData) data);
	}

	@Override
	public ForSaleSign createPlotSign(Plot plot, Sign sign, String[] lines) throws SignException {
		
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
			throw new SignException("The sign's 1st line should be \"" + getFirstLineOptions().get(0) + "\" if you want to sell a region.");
		}
		
		BlockVector coords = sign.getLocation().toVector().toBlockVector();
		
		return createPlotSign(plot, new ForSaleSignData(this, coords));
	}

}
