package com.epam.jmp.exchange.service;

import com.epam.jmp.exchange.exception.ExchangeRateNotFoundException;

import java.util.Currency;
import java.util.Map;
import java.util.Objects;

import static java.lang.Double.NaN;

/**
 * Service class to get exchange rates
 * This is a simplified version of the real service, with hardcoded exchange rates.
 * Real one should use some external service to get the rates.
 */
public class CurrencyExchangeService {

    private static final Map<String, Double> exchangeRates = new java.util.HashMap<>();

    static {
        exchangeRates.put("USD_GBP", 1.29);
        exchangeRates.put("GBP_USD", 0.78);
        exchangeRates.put("EUR_GBP", 1.18);
        exchangeRates.put("GBP_EUR", 0.85);
        exchangeRates.put("CAD_GBP", 1.73);
        exchangeRates.put("GBP_CAD", 0.58);
        exchangeRates.put("USD_EUR", 1.08);
        exchangeRates.put("EUR_USD", 0.92);
        exchangeRates.put("USD_CAD", 1.36);
        exchangeRates.put("CAD_USD", 0.74);
        exchangeRates.put("EUR_CAD", 1.47);
        exchangeRates.put("CAD_EUR", 0.68);
    }

    public double getExchangeRate(String fromCurrency, String toCurrency) throws ExchangeRateNotFoundException {

        // COP is not supported, used to simulate an exception that causes rollback
        var pesos = Currency.getInstance("COP").getCurrencyCode();
        if (Objects.equals(fromCurrency, pesos) || Objects.equals(toCurrency, pesos)) {
            return NaN;
        }

        if (Objects.equals(fromCurrency, toCurrency)) {
            return 1.0;
        } else if (!exchangeRates.containsKey(fromCurrency + "_" + toCurrency)) {
            throw new ExchangeRateNotFoundException(fromCurrency, toCurrency);
        } else {
            return exchangeRates.get(fromCurrency + "_" + toCurrency);
        }
    }

    public void setExchangeRate(String fromCurrency, String toCurrency, double rate) {
        exchangeRates.put(fromCurrency + "_" + toCurrency, rate);
    }
}
