package com.mtihc.regionselfservice.v2.plots.exceptions;

public class PlotControlException extends Exception {
    
    private static final long serialVersionUID = -3526176192609347117L;
    
    public PlotControlException() {
	
    }
    
    public PlotControlException(String message) {
	super(message);
    }
    
    public PlotControlException(Throwable cause) {
	super(cause);
    }
    
    public PlotControlException(String message, Throwable cause) {
	super(message, cause);
    }
    
}
