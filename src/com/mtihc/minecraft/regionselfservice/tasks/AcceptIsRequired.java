package com.mtihc.minecraft.regionselfservice.tasks;

public class AcceptIsRequired extends Exception {

	private static final long serialVersionUID = 2957429533177227095L;

	public AcceptIsRequired() {
		super("You must accept, if you want to proceed.");
	}
}
