package com.mtihc.regionselfservice.v2.plots.exceptions;

public class EconomyException extends Exception {

	private static final long serialVersionUID = -8873444657185085652L;

	public EconomyException() {
		
	}

	public EconomyException(String msg) {
		super(msg);
	}

	public EconomyException(Throwable cause) {
		super(cause);
	}

	public EconomyException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
