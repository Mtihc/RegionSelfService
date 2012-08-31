package com.mtihc.regionselfservice.v2.plugin;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.util.BlockVector;

import com.mtihc.regionselfservice.v2.plots.ISignValidator;
import com.mtihc.regionselfservice.v2.plots.data.ISignData;
import com.mtihc.regionselfservice.v2.plots.data.SignDataForRent;
import com.mtihc.regionselfservice.v2.plots.data.SignDataForSale;
import com.mtihc.regionselfservice.v2.plots.data.SignType;
import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;

public class SignValidator implements ISignValidator {

	private List<String> firstLinesForRent;
	private List<String> firstLinesForSale;

	public SignValidator(List<String> firstLinesForRent, List<String> firstLinesForSale) {
		this.firstLinesForRent = firstLinesForRent;
		this.firstLinesForSale = firstLinesForSale;
		Bukkit.getLogger().info(firstLinesForRent + " " + firstLinesForSale);
	}

	@Override
	public boolean isPlotSign(Sign sign, String[] lines) {
		if(sign == null) {
			return false;
		}
		String firstLine;
		try {
			firstLine = lines[0];
		} catch(Exception e) {
			firstLine = "";
		}
		
		Bukkit.getLogger().info("firstLine is \"" + firstLine + "\"");
		Bukkit.getLogger().info(" " + firstLinesForSale);
		for (String line : firstLinesForRent) {
			if(line.equalsIgnoreCase(firstLine)) {
				return true;
			}
		}
		for (String line : firstLinesForSale) {
			if(line.equalsIgnoreCase(firstLine)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ISignData createPlotSign(Sign sign, String[] lines) throws SignException {
		if(sign == null) {
			return null;
		}
		
		SignType type = null;
		String firstLine;
		try {
			firstLine = lines[0];
		} catch(Exception e) {
			firstLine = "";
		}

		if(type == null) {
			for (String line : firstLinesForRent) {
				if(line.equalsIgnoreCase(firstLine)) {
					type = SignType.FOR_RENT;
					lines[0] = line;
					break;
				}
			}
		}
		if(type == null) {
			for (String line : firstLinesForSale) {
				if(line.equalsIgnoreCase(firstLine)) {
					type = SignType.FOR_SALE;
					lines[0] = line;
					break;
				}
			}
		}
		if(type == null) {
			throw new SignException("Expected a different first line. For example: \"" + firstLinesForRent.get(0) + "\" or \"" + firstLinesForSale.get(0) + "\".");
		}
		
		
		
		double cost;
		try {
			cost = Double.parseDouble(lines[1].trim());
		} catch(NumberFormatException e) {
			if(lines[1].trim().equalsIgnoreCase("free")) {
				cost = 0;
			}
			else {
				throw new SignException("Couldn't find the cost on line 2.");
			}
		}
		cost = Math.max(0, cost);

		if(cost == 0) {
			lines[1] = "FREE";
		}
		
		BlockVector coords = sign.getLocation().toVector().toBlockVector();
		BlockFace attachedFace = ((org.bukkit.material.Sign)sign.getData()).getAttachedFace();
		String regionId = getRegionIdFromSign(lines);
		
		ISignData result = null;
		
		switch (type) {
		case FOR_RENT:
			
			result = new SignDataForRent(coords, attachedFace, regionId, cost);
			break;
		case FOR_SALE:
			
			result = new SignDataForSale(coords, attachedFace, regionId, cost);
			break;
		default:
			
			throw new SignException("Unknown reward type: " + type);
		}
		for (int i = 0; i < lines.length && i < 4; i++) {
			sign.setLine(i, lines[1].trim());
		}
		return result;
	}

	private static final int LINE_INDEX_REGION = 2;
	
	private String getRegionIdFromSign(String[] lines) throws SignException {

		
		
		String regionName;
		try {
			regionName = lines[LINE_INDEX_REGION].trim();
		} catch(NullPointerException e) {
			regionName = null;
		} catch(IndexOutOfBoundsException e) {
			regionName = null;
		}
		
		if(regionName == null || regionName.isEmpty()) {
			throw new SignException("Couldn't find region name on line 3 (and 4).");
		}
		
		int n = lines.length;
		for (int i = LINE_INDEX_REGION + 1; i < n; i++) {
			regionName += lines[i].trim();
		}
		
		return regionName;
	}

}
