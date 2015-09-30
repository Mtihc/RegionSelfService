package com.mtihc.regionselfservice.v2.plots.exceptions;

public class PlotBoundsException extends PlotControlException {
    
    private static final long serialVersionUID = -9136937024689204439L;
    
    public enum Type {
	SELECTION_TOO_SMALL("The selected region is too small..."),
	SELECTION_TOO_BIG("The selected region is too big!"),
	SELECTION_TOO_LOW("The bottom-y coordinate is too low."),
	SELECTION_TOO_HIGH("The top-y coordinate is too high");
	
	private String message;
	
	private Type(String msg) {
	    this.message = msg;
	}
	
	public String getMessage() {
	    return this.message;
	}
	
    }
    
    private Type type;
    
    public PlotBoundsException(Type type) {
	super(type.getMessage());
	this.type = type;
    }
    
    public PlotBoundsException(Type type, int topY, int bottomY, int min, int max) {
	super(getMessage(type, topY, bottomY, min, max));
	this.type = type;
    }
    
    static private String getMessage(Type type, int topY, int bottomY, int min, int max) {
	if (type.equals(Type.SELECTION_TOO_LOW)) {
	    return type.getMessage() + " The bottom-y coordinate is " + bottomY + ". But shouldn't be lower than " + min;
	} else if (type.equals(Type.SELECTION_TOO_HIGH)) {
	    return type.getMessage() + " The top-y coordinate is " + topY + ". But shouldn't be greater than " + max;
	} else {
	    return "You selection's top-y coordinate is too high, or the bottom-y is too low.";
	}
    }
    
    public PlotBoundsException(Type type, int width, int length, int height, int minimum, int maximum, int minHeight, int maxHeight) {
	super(getMessage(type, width, length, height, minimum, maximum, minHeight, maxHeight));
	this.type = type;
    }
    
    static private String getMessage(Type type, int width, int length, int height, int minimum, int maximum, int minHeight, int maxHeight) {
	if (type.equals(Type.SELECTION_TOO_BIG)) {
	    return type.getMessage() + " Your selection's size is " + width + "x" + length + "x" + height + ". But the maximum width/length/height is " + maximum + "x" + maximum + "x" + maxHeight + ".";
	} else if (type.equals(Type.SELECTION_TOO_SMALL)) {
	    return type.getMessage() + " Your selection's size is " + width + "x" + length + "x" + height + ". But the minimum is " + minimum + "x" + minimum + "x" + minHeight + ".";
	} else {
	    return "Your selection's size is too big or too small. Your selection's size is " + width + "x" + length + "x" + height + ".";
	}
    }
    
    public PlotBoundsException(Type type, String regionName, String worldName) {
	super(getMessage(type, regionName, worldName));
	this.type = type;
    }
    
    static private String getMessage(Type type, String regionName, String worldName) {
	return type.getMessage().replace("<region name>", regionName).replace("<world>", worldName);
    }
    
    public Type getType() {
	return this.type;
    }
    
}
