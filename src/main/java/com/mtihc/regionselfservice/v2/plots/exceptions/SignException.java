package com.mtihc.regionselfservice.v2.plots.exceptions;

public class SignException extends Exception {

	private static final long serialVersionUID = -8873444657185085651L;

	public SignException() {
		
	}

	public SignException(String msg) {
		super(msg);
	}

	public SignException(Throwable cause) {
		super(cause);
	}

	public SignException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
