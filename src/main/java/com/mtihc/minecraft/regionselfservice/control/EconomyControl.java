package com.mtihc.minecraft.regionselfservice.control;

import com.mtihc.minecraft.regionselfservice.exceptions.PaymentException;

public interface EconomyControl {
	void deposit(String account, double amount) throws PaymentException;
	void withdraw(String account, double amount) throws PaymentException;
	double getBalance(String account);
	String format(double amount);
	String getName();
}
