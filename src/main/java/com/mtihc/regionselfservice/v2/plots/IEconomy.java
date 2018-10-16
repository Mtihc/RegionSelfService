package com.mtihc.regionselfservice.v2.plots;

import com.mtihc.regionselfservice.v2.plots.exceptions.EconomyException;


public interface IEconomy {
    
    void deposit(String account, double amount);
    
    void withdraw(String account, double amount) throws EconomyException;
    
    double getBalance(String account);
    
    String format(double amount);
    
    String getName();
}
