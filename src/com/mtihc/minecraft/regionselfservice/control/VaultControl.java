package com.mtihc.minecraft.regionselfservice.control;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.mtihc.minecraft.regionselfservice.exceptions.EconomyInstantiateException;
import com.mtihc.minecraft.regionselfservice.exceptions.NotEnoughMoneyException;
import com.mtihc.minecraft.regionselfservice.exceptions.PaymentException;

public class VaultControl implements EconomyControl {

	private Economy economy;
	
	public VaultControl(Server server) throws EconomyInstantiateException {
		if(!setupEconomy(server)) {
			throw new EconomyInstantiateException(getPluginName(), "Could not find economy plugin and/or Vault.");
		}
	}
	
	private String getPluginName() {
		return "Vault";
	}

	private boolean setupEconomy(Server server) {
		economy = null;
		RegisteredServiceProvider<Economy> economyProvider;
		try {
			economyProvider = server.getServicesManager().getRegistration(
					net.milkbowl.vault.economy.Economy.class);
		} catch (NoClassDefFoundError e) {
			economyProvider = null;
		} catch (IncompatibleClassChangeError e) {
			economyProvider = null;
		} catch (NullPointerException e) {
			economyProvider = null;
		}
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	@Override
	public void deposit(String account, double amount) throws PaymentException {
		EconomyResponse response = economy.depositPlayer(account, amount);
		if(!response.transactionSuccess()) {
			throw new PaymentException(PaymentException.Type.DEPOSIT_ERROR, response.errorMessage);
		}
	}

	@Override
	public void withdraw(String account, double amount) throws NotEnoughMoneyException, PaymentException {
		EconomyResponse response = economy.withdrawPlayer(account, amount);
		if(!response.transactionSuccess()) {
			if(amount > response.balance) {
				throw new NotEnoughMoneyException("You still require " + economy.format(amount - response.balance) + ".", response.balance, response.amount);
			}
			else {
				throw new PaymentException(PaymentException.Type.WITHDRAW_ERROR, response.errorMessage);
			}
		}
	}

	@Override
	public double getBalance(String account) {
		return economy.getBalance(account);
	}

	@Override
	public String format(double amount) {
		return economy.format(amount);
	}

	@Override
	public String getName() {
		return economy.getName();
	}
}
