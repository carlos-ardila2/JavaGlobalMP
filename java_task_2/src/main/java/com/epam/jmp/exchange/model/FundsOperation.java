package com.epam.jmp.exchange.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

public class FundsOperation {

    private String fromAccountNumber;
    private String toAccountNumber;

    private Currency fromCurrency;
    private Currency toCurrency;

    private BigDecimal operationAmount;
    private BigDecimal exchangeResultingAmount;

    private Double currencyExchangeRate;

    private LocalDateTime operationDate;
    private final Type type;

    public FundsOperation(Type type) {
        this.type = type;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public BigDecimal getOperationAmount() {
        return operationAmount;
    }

    public BigDecimal getExchangeResultingAmount() {
        return exchangeResultingAmount;
    }

    public Double getCurrencyExchangeRate() {
        return currencyExchangeRate;
    }

    public Type getType() {
        return type;
    }

    public LocalDateTime getOperationDate() {
        return operationDate;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }

    public void setOperationAmount(BigDecimal operationAmount) {
        this.operationAmount = operationAmount;
    }

    public void setExchangeResultingAmount(BigDecimal exchangeResultingAmount) {
        this.exchangeResultingAmount = exchangeResultingAmount;
    }

    public void setCurrencyExchangeRate(Double currencyExchangeRate) {
        this.currencyExchangeRate = currencyExchangeRate;
    }

    public void setOperationDate(LocalDateTime operationDate) {
        this.operationDate = operationDate;
    }

    public enum Type {
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
    }
}
