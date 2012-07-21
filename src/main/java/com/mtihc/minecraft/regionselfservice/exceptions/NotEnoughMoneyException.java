package com.mtihc.minecraft.regionselfservice.exceptions;

public class NotEnoughMoneyException extends PaymentException {

	private static final long serialVersionUID = 6438391105549119532L;
	
	private double balance;
	private double price;
	private double required;

	public NotEnoughMoneyException(String message, double balance, double price) {
		super(Type.WITHDRAW_ERROR, message);
		this.balance = balance;
		this.price = price;
		this.required = price - balance;
		if(required < 0) {
			required = 0;
		}
	}
	
	public double getBalance() {
		return balance;
	}
	
	public double getPrice() {
		return price;
	}
	
	public double getRequired() {
		return required;
	}
}
