package com.mtihc.minecraft.regionselfservice.exceptions;


public class PaymentException extends Exception {

	private static final long serialVersionUID = -2059293508106519005L;

	public enum Type {
		DEPOSIT_ERROR("Unable to deposit on the receiver's account."),
		WITHDRAW_ERROR("Unable to withdraw from the sender's account.");
		
		private String defaultMessage;

		Type(String defaultMessage) {
			this.defaultMessage = defaultMessage;
		}
		
		public String getDefaultMessage() {
			return defaultMessage;
		}
	}

	private Type type;
	
	public PaymentException(Type type, String message, Throwable cause) {
		super(message, cause);
		this.type = type;
	}

	public PaymentException(Type type, String message) {
		super(message);
		this.type = type;
	}

	public PaymentException(Type type, Throwable cause) {
		super(type.getDefaultMessage(), cause);
		this.type = type;
	}

	public Type getType() {
		return type;
	}

}
