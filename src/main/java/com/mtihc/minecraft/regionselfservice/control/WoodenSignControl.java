package com.mtihc.minecraft.regionselfservice.control;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mtihc.minecraft.regionselfservice.exceptions.WoodenSignException;

public class WoodenSignControl {

	public static final int LINE_INDEX_REGION = 2;
	public static final int LINE_INDEX_COST = 1;
	private ConfigControl configControl;
	
	
	public WoodenSignControl(ConfigControl configControl) {
		this.configControl = configControl;
	}
	

	/**
	 * Returns whether the specified block is a sign
	 * 
	 * @param block
	 *            The block to check
	 * @return Whether the block is a sign
	 */
	public boolean isSign(Block block) {
		if (block != null
				&& (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Finds and returns the region name on a sign
	 * 
	 * @param lines
	 *            The lines of text on the sign
	 * @return The region name on the sign, or null
	 */
	public String getRegionNameOnSign(String[] lines) throws WoodenSignException {

		String regionName;
		try {
			regionName = lines[LINE_INDEX_REGION].trim();
		} catch(NullPointerException e) {
			regionName = null;
		} catch(IndexOutOfBoundsException e) {
			regionName = null;
		}
		
		if(regionName == null) {
			throw new WoodenSignException(WoodenSignException.Type.NO_REGION_NAME, LINE_INDEX_REGION + 1);
		}
		
		int n = lines.length;
		for (int i = LINE_INDEX_REGION + 1; i < n; i++) {
			regionName += lines[i].trim();
		}
		
		return regionName;
	}
	
	public double getRegionCostOnSign(String[] lines) throws WoodenSignException {
		double cost;
		try {
			cost = Double.parseDouble(lines[LINE_INDEX_COST].trim());
		} catch(NullPointerException e) {
			throw new WoodenSignException(WoodenSignException.Type.NO_COST, LINE_INDEX_COST + 1);
		} catch(IndexOutOfBoundsException e) {
			throw new WoodenSignException(WoodenSignException.Type.NO_COST, LINE_INDEX_COST + 1);
		} catch(NumberFormatException e) {
			if(lines[LINE_INDEX_COST].equalsIgnoreCase("free")) {
				cost = 0;
			}
			else {
				throw new WoodenSignException(WoodenSignException.Type.NO_COST, LINE_INDEX_COST + 1);
			}
		}
		return cost;
	}
	
	/**
	 * Returns whether the specified string is null or empty
	 * 
	 * @param string
	 *            The string to check
	 * @return Whether the specified string is null or empty
	 */
	public boolean isEmptyString(String string) {
		return string == null || string.trim().matches("\\s*");
	}
	
	public boolean matchFirstLine(List<String> list, String firstLine) {
		for (Object line : list) {
			if (line.toString().equalsIgnoreCase(firstLine)) {
				return true;
			}
		}
		return false;
	}

	

	/**
	 * Removes the specified block and drops a sign item
	 * 
	 * @param block
	 *            The block to break
	 * @param dropItem
	 *            Whether to drop a sign item
	 */
	public void breakSign(Block block, boolean dropItem) {

		if (!isSign(block)) {
			return;
		}
		
		Sign sign = (Sign) block.getState();
		boolean isSaleSign = matchFirstLine(configControl.settings().getFirstLineForSale(), sign.getLine(0));
		boolean isRentSign = matchFirstLine(configControl.settings().getFirstLineForRent(), sign.getLine(0));
		String regionName;
		try {
			regionName = getRegionNameOnSign(sign.getLines());
		} catch (WoodenSignException e) {
			regionName = null;
		}
		if(regionName != null && (isSaleSign || isRentSign)) {
			
			if(isSaleSign) {
				configControl.signsSale().clearRegionSign(block.getWorld().getName(), regionName, block.getX(), block.getY(), block.getZ());
			}
			else if(isRentSign) {
				configControl.signsRent().clearRegionSign(block.getWorld().getName(), regionName, block.getX(), block.getY(), block.getZ());
			}
		}
		
		
		// clear block
		block.setType(Material.AIR);
		// drop sign item
		if (dropItem) {
			block.getWorld().dropItemNaturally(block.getLocation(),
					new ItemStack(Material.SIGN, 1));
		}
		
	}

	public void breakAllSaleSigns(String regionName, World world,
			boolean dropItem) {
		List<Vector> blocks = configControl.signsSale().getRegionSigns(world.getName(), regionName);
		configControl.signsSale().clearRegion(world.getName(), regionName);
		configControl.signsSale().save();
		if (blocks != null) {
			for (Vector value : blocks) {
				Block block = world
						.getBlockAt(value.getBlockX(), value.getBlockY(),
								value.getBlockZ());
				breakSign(block, dropItem);
			}
		}
	}

	public void breakAllRentSigns(String regionName, World world,
			boolean dropItem) {
		List<Vector> blocks = configControl.signsRent().getRegionSigns(world.getName(), regionName);
		configControl.signsRent().clearRegion(world.getName(), regionName);
		configControl.signsRent().save();
		if (blocks != null) {
			for (Vector value : blocks) {
				Block block = world
						.getBlockAt(value.getBlockX(), value.getBlockY(),
								value.getBlockZ());
				breakSign(block, dropItem);
			}
		}
	}
}
