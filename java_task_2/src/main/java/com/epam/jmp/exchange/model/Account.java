package com.epam.jmp.exchange.model;

import com.epam.jmp.exchange.exception.NotEnoughFundsException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.Set;

public class Account implements Serializable {

    private final String accountNumber;
    private final String customerName;

    private final Map<Currency, BigDecimal> currenciesBalance = new java.util.HashMap<>();

    public Account(String accountNumber, String customerName) {
        this.accountNumber = accountNumber;
        this.customerName = customerName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getBalance(Currency currency) {
        return currenciesBalance.get(currency);
    }

    public BigDecimal addBalance(Currency currency, BigDecimal amount) {
        var newBalance = currenciesBalance.getOrDefault(currency, BigDecimal.ZERO).add(amount);
        currenciesBalance.put(currency, newBalance);
        return newBalance;
    }

    public BigDecimal subtractBalance(Currency currency, BigDecimal amount) throws NotEnoughFundsException {
        var currentBalance = currenciesBalance.get(currency);

        if (currentBalance == null) {
            throw new NotEnoughFundsException(accountNumber, currency);
        }

        if (currentBalance.compareTo(amount) < 0) {
            throw new NotEnoughFundsException(accountNumber, currency, amount);
        }
        var newBalance = currentBalance.subtract(amount);
        currenciesBalance.replace(currency, newBalance);
        return newBalance;
    }

    public Set<Currency> getAvailableCurrencies() {
        return currenciesBalance.keySet();
    }
}
