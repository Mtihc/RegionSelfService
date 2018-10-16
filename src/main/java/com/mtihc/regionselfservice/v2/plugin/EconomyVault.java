package com.mtihc.regionselfservice.v2.plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import com.mtihc.regionselfservice.v2.plots.IEconomy;
import com.mtihc.regionselfservice.v2.plots.exceptions.EconomyException;


public class EconomyVault implements IEconomy {
    
    private Economy econ;
    private Logger logger;
    
    public EconomyVault(Economy econ, Logger logger) {
	this.econ = econ;
	this.logger = logger;
    }
    
    @Override
    public void deposit(String account, double amount) {
	EconomyResponse res = this.econ.depositPlayer(account, amount);
	if (!res.transactionSuccess()) {
	    this.logger.log(Level.WARNING, "Failed to deposit " + amount + " to " + account + ": " + res.errorMessage);
	}
    }
    
    @Override
    public void withdraw(String account, double amount) throws EconomyException {
	EconomyResponse res = this.econ.withdrawPlayer(account, amount);
	if (!res.transactionSuccess()) {
	    if (res.balance >= amount) {
		// it's not the balance
		this.logger.log(Level.WARNING, "Failed to withdraw " + amount + " from " + account + ": " + res.errorMessage);
	    }
	    throw new EconomyException(res.errorMessage);
	    
	}
    }
    
    @Override
    public double getBalance(String account) {
	return this.econ.getBalance(account);
    }
    
    @Override
    public String format(double amount) {
	return this.econ.format(amount);
    }
    
    @Override
    public String getName() {
	return this.econ.getName();
    }
    
}
