package com.mtihc.regionselfservice.v2.plots.util;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TimeStringConverter {
    
    public static Pattern PATTERN_DAYS = Pattern.compile("(\\d+)d");
    public static Pattern PATTERN_HOURS = Pattern.compile("(\\d+)h");
    public static Pattern PATTERN_MINUTES = Pattern.compile("(\\d+)m");
    
    public String convert(long millisec) {
	if (millisec <= 0) {
	    return "0m";
	}
	
	int minutes = (int) Math.floor(millisec / 60000);
	int hours = (int) Math.floor(minutes / 60);
	minutes = minutes % 60;
	int days = (int) Math.floor(hours / 24);
	hours = hours % 24;
	
	String result = days + "d" + hours + "h" + minutes + "m";
	result = result.replaceAll("0[dhm]", "");
	if (result.isEmpty()) {
	    result = "0m";
	}
	return result;
    }
    
    public long convert(String timeString) {
	if (timeString == null) {
	    return 0;
	}
	String input = timeString.replace("\\s", "");
	
	Matcher daysMatcher = PATTERN_DAYS.matcher(input), hoursMatcher = PATTERN_HOURS.matcher(input), minutesMatcher = PATTERN_MINUTES.matcher(input);
	daysMatcher.find();
	hoursMatcher.find();
	minutesMatcher.find();
	
	long result = 0;
	try {
	    result += TimeUnit.DAYS.toMillis(Integer.parseInt(input.substring(daysMatcher.start(), daysMatcher.end() - 1)));
	} catch (Exception e) {
	    
	}
	try {
	    result += TimeUnit.HOURS.toMillis(Integer.parseInt(input.substring(hoursMatcher.start(), hoursMatcher.end() - 1)));
	} catch (Exception e) {
	    
	}
	try {
	    result += TimeUnit.MINUTES.toMillis(Integer.parseInt(input.substring(minutesMatcher.start(), minutesMatcher.end() - 1)));
	} catch (Exception e) {
	    
	}
	return result;
    }
    
}
