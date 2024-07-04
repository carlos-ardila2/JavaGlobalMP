package com.epam.jmp.exchange.exception;

import java.math.BigDecimal;
import java.util.Currency;

public class NotEnoughFundsException extends Exception {
    public NotEnoughFundsException(String accountNumber, Currency operationCurrency, BigDecimal operationAmount) {
        super(String.format("Can't withdraw %.2f %s from account %s. Not enough funds!", operationAmount, operationCurrency,
                accountNumber));
    }

    public NotEnoughFundsException(String accountNumber, Currency currency) {
        super(String.format("Account %s does not have balance in %s currency!", accountNumber, currency));
    }
}
