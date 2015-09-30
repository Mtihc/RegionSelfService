package com.mtihc.regionselfservice.v2.plots.signs;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import com.mtihc.regionselfservice.v2.plots.IPlotSignData;
import com.mtihc.regionselfservice.v2.plots.Plot;
import com.mtihc.regionselfservice.v2.plots.PlotWorld;
import com.mtihc.regionselfservice.v2.plots.exceptions.SignException;
import com.mtihc.regionselfservice.v2.plots.util.TimeStringConverter;


public abstract class PlotSignText<T extends IPlotSignData> {
    
    // the region id is always line 3 and 4,
    // so we place this static method in abstract PlotSignText class
    // instead of ForSaleSignText or ForRentSignText
    /**
     * Get the region id. Given the lines on a wooden sign
     * 
     * @param lines
     *        The lines of text on a wooden sign
     * @return The region id that is on line 3 and/or 4
     */
    public static String getRegionId(String[] lines) {
	String result = "";
	for (int i = 2; i < lines.length; i++) {
	    result += lines[i].trim();
	}
	if (result.isEmpty()) {
	    return null;
	} else {
	    return result;
	}
    }
    
    public static PlotSignType getPlotSignType(String[] lines) {
	// get first string from array
	String firstLine;
	try {
	    firstLine = lines[0];
	} catch (Exception e) {
	    return null;
	}
	// find corresponding type
	PlotSignType[] types = PlotSignType.values();
	for (PlotSignType type : types) {
	    if (type.isFirstLineOption(firstLine)) {
		return type;
	    }
	}
	return null;
    }
    
    /**
     * Create the object that is a subclass of <code>PlotSignText</code>. Could be <code>ForSaleSignText</code> or <code>ForRentSignText</code>.
     * 
     * @param plotWorld
     *        The world data
     * @param lines
     *        The lines of text on the wooden sign
     * @return The object that is a subclass of <code>PlotSignText</code>.
     * @throws SignException
     *         when there's incorrect text on the sign.
     */
    public static PlotSignText<?> createText(PlotWorld plotWorld, String[] lines) throws SignException {
	PlotSignType type = getPlotSignType(lines);
	if (type == PlotSignType.FOR_RENT) {
	    return new ForRentSignText(plotWorld, lines);
	} else if (type == PlotSignType.FOR_SALE) {
	    return new ForSaleSignText(plotWorld, lines);
	} else {
	    throw new SignException("Not a valid plot sign. Could not find the matching sign type.");
	}
    }
    
    public abstract void applyToSign(String[] lines);
    
    public void applyToSign(Sign sign) {
	applyToSign(sign.getLines());
	sign.update();
    }
    
    public void applyToSign(SignChangeEvent event) {
	applyToSign(event.getLines());
    }
    
    private PlotWorld plotWorld;
    private PlotSignType type;
    private String regionId;
    private Plot plot;
    
    /**
     * Constructor.
     * 
     * @param plotWorld
     *        The data of the world
     * @param type
     *        The type of sign
     * @param regionId
     *        The region id
     */
    public PlotSignText(PlotWorld plotWorld, PlotSignType type, String regionId) {
	this.plotWorld = plotWorld;
	this.type = type;
	this.regionId = regionId;
	this.plot = plotWorld.getPlot(regionId);
    }
    
    /**
     * Base constructor.
     * 
     * @param plotWorld
     *        The data of the world
     * @param lines
     *        The lines on the sign
     * @throws SignException
     *         thrown when the lines on the sign are invalid
     */
    public PlotSignText(PlotWorld plotWorld, String[] lines) throws SignException {
	this.plotWorld = plotWorld;
	this.type = getPlotSignType(lines);
	if (this.type == null) {
	    throw new SignException("Not a valid plot sign. Could not find the matching sign type.");
	}
	this.regionId = getRegionId(lines);
	if (this.regionId == null) {
	    throw new SignException("Region name is not specified on lines 3 and/or 4.");
	}
	Plot plot = plotWorld.getPlot(this.regionId);
	if (plot == null || plot.getRegion() == null) {
	    throw new SignException("Region '" + this.regionId + "' does not exist.");
	}
	this.plot = plotWorld.getPlot(this.regionId);
    }
    
    /**
     * The type of sign
     * 
     * @return the type
     */
    public PlotSignType getPlotSignType() {
	return this.type;
    }
    
    /**
     * The region id on the sign
     * 
     * @return the region id
     */
    public String getRegionId() {
	return this.regionId;
    }
    
    public PlotWorld getPlotWorld() {
	return this.plotWorld;
    }
    
    public Plot getPlot() {
	return this.plot;
    }
    
    //
    // ForSaleSignText
    //
    public static class ForSaleSignText extends PlotSignText<ForSaleSignData> {
	
	public static double getSellCost(String[] lines) throws SignException {
	    try {
		return Math.max(0, Double.parseDouble(lines[1]));
	    } catch (Exception e) {
		throw new SignException("There is no cost specified on line 2.");
	    }
	}
	
	public void setSellCost(String[] lines, double cost) {
	    String costString;
	    if (cost % 1 == 0) {
		costString = String.valueOf((int) cost);
	    } else {
		costString = String.valueOf(cost);
	    }
	    
	    lines[1] = costString;
	}
	
	private double sellCost;
	
	/**
	 * Constructor.
	 * 
	 * @param plotWorld
	 *        The data of the world
	 * @param regionId
	 *        The region id
	 * @param cost
	 *        The sell cost
	 */
	public ForSaleSignText(PlotWorld plotWorld, String regionId, double cost) {
	    super(plotWorld, PlotSignType.FOR_SALE, regionId);
	    this.sellCost = cost;
	}
	
	/**
	 * Constructor
	 * 
	 * @param plotWorld
	 *        The data of the world
	 * @param lines
	 *        the lines on the sign
	 * @throws SignException
	 *         thrown when the lines on the sign are invalid
	 */
	public ForSaleSignText(PlotWorld plotWorld, String[] lines) throws SignException {
	    super(plotWorld, lines);
	    try {
		this.sellCost = getSellCost(lines);
	    } catch (SignException e) {
		// failed to find cost on the sign...
		// assume that the player did not type anything.
		// use default sell cost
		this.sellCost = getPlot().getSellCost();
	    }
	}
	
	/**
	 * The sell cost on the sign
	 * 
	 * @return the sell cost
	 */
	public double getSellCost() {
	    return this.sellCost;
	}
	
	public void setSellCost(double value) {
	    this.sellCost = value;
	}
	
	@Override
	public void applyToSign(String[] lines) {
	    setSellCost(lines, getSellCost());
	}
	
    }
    
    //
    // ForRentSignText
    //
    public static class ForRentSignText extends PlotSignText<ForRentSignData> {
	
	private static final String separator = ":";
	
	public static void setRentCost(String[] lines, double cost, long millisec) {
	    // convert cost to string
	    String costString;
	    if (cost % 1 == 0) {
		costString = String.valueOf((int) cost);// remove the .0
	    } else {
		costString = String.valueOf(cost);
	    }
	    
	    // convert time to string, like 1d23h59m
	    String timeString = new TimeStringConverter().convert(millisec);
	    
	    // set the text on the correct line,
	    // use the cost and time
	    lines[1] = costString + separator + timeString;
	}
	
	public static void setRentPlayerTime(String[] lines, String playerName, long millisec) {
	    // convert time to string, like 1d23h59m
	    String timeString = new TimeStringConverter().convert(millisec);
	    
	    // set the text on the correct line,
	    // use the player name, instead of cost.
	    lines[1] = playerName + separator + timeString;
	}
	
	private double rentCost;
	private long rentTime;
	private String rentPlayer;
	private long rentPlayerTime;
	
	public ForRentSignText(PlotWorld plotWorld, String regionId) {
	    super(plotWorld, PlotSignType.FOR_RENT, regionId);
	    this.rentCost = getPlot().getRentCost();
	    this.rentTime = getPlot().getRentTime();
	}
	
	public ForRentSignText(PlotWorld plotWorld, String regionId, String rentPlayer, long rentPlayerTime) {
	    this(plotWorld, regionId);
	    this.rentPlayer = rentPlayer;
	    this.rentPlayerTime = rentPlayerTime;
	}
	
	/**
	 * Constructor
	 * 
	 * @param plotWorld
	 *        The data of the world
	 * @param lines
	 *        the lines on the sign
	 * @throws SignException
	 *         thrown when the lines on the sign are invalid
	 */
	public ForRentSignText(PlotWorld plotWorld, String[] lines) throws SignException {
	    super(plotWorld, lines);
	    Plot plot = plotWorld.getPlot(getRegionId());
	    
	    // get time
	    long time;
	    try {
		// get text after separator, and convert string to milliseconds
		time = new TimeStringConverter().convert(lines[1].split(separator)[1].trim());
	    } catch (Exception e) {
		time = 0;
	    }
	    if (time <= 0) {
		// assume player typed nothing,
		// use old rent time
		time = plot.getRentTime();
	    }
	    // get cost or player name
	    String costOrPlayer;
	    try {
		// get text before separator
		costOrPlayer = lines[1].split(separator)[0].trim();
		
		try {
		    // try to convert text to number
		    this.rentCost = Double.parseDouble(costOrPlayer);
		    // converting did not fail, this must be the cost number
		    // so the time must be the rent time
		    this.rentTime = time;
		} catch (NumberFormatException e) {
		    // converting failed, this must be a player name
		    this.rentPlayer = costOrPlayer;
		    // so the time must be the remaining rent time for the player
		    this.rentPlayerTime = time;
		}
	    } catch (Exception e) {
		// assume player typed nothing,
		// use old rent cost
		this.rentCost = plot.getRentCost();
		this.rentTime = time;
	    }
	}
	
	/**
	 * Whether the sign is used by a player to rent the region
	 * 
	 * @return whether the region is rented via the sign
	 */
	public boolean isRentedOut() {
	    return this.rentPlayer != null;
	}
	
	/**
	 * The rent cost on the sign. Only defined when not rented out.
	 * 
	 * @return the rent cost
	 */
	public double getRentCost() {
	    return this.rentCost;
	}
	
	public void setRentCost(double value) {
	    this.rentCost = value;
	}
	
	/**
	 * The rent time on the sign. Only defined when not rented out.
	 * 
	 * @return the rent time
	 */
	public long getRentTime() {
	    return this.rentTime;
	}
	
	public void setRentTime(long value) {
	    this.rentTime = value;
	}
	
	/**
	 * The player that rented the region via this sign. Only defined when rented out.
	 * 
	 * @return the renter's name
	 */
	public String getRentPlayer() {
	    return this.rentPlayer;
	}
	
	public void setRentPlayer(String value) {
	    this.rentPlayer = value;
	}
	
	/**
	 * The time remaining for the player that rented the region via this sign. Only defined when rented out.
	 * 
	 * @return the time remaining
	 */
	public long getRentPlayerTime() {
	    return this.rentPlayerTime;
	}
	
	public void setRentPlayerTime(long value) {
	    this.rentPlayerTime = value;
	}
	
	@Override
	public void applyToSign(String[] lines) {
	    if (isRentedOut()) {
		setRentPlayerTime(lines, getRentPlayer(), getRentPlayerTime());
	    } else {
		setRentCost(lines, getRentCost(), getRentTime());
	    }
	}
    }
}
