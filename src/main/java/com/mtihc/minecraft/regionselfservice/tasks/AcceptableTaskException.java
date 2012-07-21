package com.mtihc.minecraft.regionselfservice.tasks;

public class AcceptableTaskException extends Exception {

	private static final long serialVersionUID = 3151187801857571005L;

	public AcceptableTaskException(String message) {
		super(message);
	}

	public AcceptableTaskException(String message, Throwable cause) {
		super(message, cause);
	}

}
