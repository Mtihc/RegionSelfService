package com.mtihc.regionselfservice.v2.plugin.util.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	
	String[] aliases();
	
	String args();
	
	String desc();
	
	String[] help();

	String perm();// default "";
}
