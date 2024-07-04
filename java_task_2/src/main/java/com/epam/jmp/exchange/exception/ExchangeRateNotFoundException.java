package com.epam.jmp.exchange.exception;

public class ExchangeRateNotFoundException extends Exception {
    public ExchangeRateNotFoundException(String fromCurrency, String toCurrency) {
        super(String.format("No exchange rate found for %s to %s", fromCurrency, toCurrency));
    }
}
