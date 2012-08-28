package com.mtihc.regionselfservice.v2.plugin.util.commands;

public class CommandPermissionException extends CommandException {
	
	private static final long serialVersionUID = -4778069147590623205L;

	public CommandPermissionException() {
		
	}

	public CommandPermissionException(String msg) {
		super(msg);
	}

	public CommandPermissionException(Throwable msg) {
		super(msg);
	}

	public CommandPermissionException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
