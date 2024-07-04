package com.epam.jmp.exchange.service;

import com.epam.jmp.exchange.dao.AccountDAO;
import com.epam.jmp.exchange.dao.FundsOperationDAO;
import com.epam.jmp.exchange.exception.ExchangeRateNotFoundException;
import com.epam.jmp.exchange.exception.NotEnoughFundsException;
import com.epam.jmp.exchange.exception.MissingAccountException;
import com.epam.jmp.exchange.model.Account;
import com.epam.jmp.exchange.model.FundsOperation;

import java.util.logging.*;
import java.math.BigDecimal;

public class AccountService {

    private final AccountDAO accountDAO;
    private final FundsOperationDAO fundsOperationDAO;
    private final CurrencyExchangeService exchangeService;

    private static final Logger LOGGER = Logger.getLogger(AccountService.class.getName());

    static {
        LOGGER.setUseParentHandlers(false);
        var handler = new ConsoleHandler();
        Formatter formatter = new SimpleFormatter() {

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format("[%1$-7s] %2$s %n", lr.getLevel().getLocalizedName(), lr.getMessage());
            }
        };
        handler.setFormatter(formatter);
        LOGGER.addHandler(handler);
    }

    public AccountService(AccountDAO accountDAO, FundsOperationDAO fundsOperationDAO, CurrencyExchangeService exchangeService) {
        this.accountDAO = accountDAO;
        this.fundsOperationDAO = fundsOperationDAO;
        this.exchangeService = exchangeService;
    }

    public Account findAccount(String id) throws MissingAccountException {
        var account = accountDAO.findById(id);
        if (account == null) {
            throw new MissingAccountException(id);
        }
        return account;
    }

    public Account createAccount(String name) {
        return accountDAO.create(name);
    }

    public void depositFunds(FundsOperation operation) {
        try {
            var newBalance = findAccount(operation.getToAccountNumber()).addBalance(operation.getToCurrency(),
                    operation.getOperationAmount());
            fundsOperationDAO.save(operation);
            LOGGER.info(String.format("DEPOSIT: Account[%s] Amount[%.2f %s] New balance[%.2f]", operation.getToAccountNumber(),
                    operation.getOperationAmount(), operation.getToCurrency().getCurrencyCode(), newBalance));
        } catch (MissingAccountException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    public void withdrawFunds(FundsOperation operation) {
        try {
            var newBalance = findAccount(operation.getFromAccountNumber()).subtractBalance(operation.getFromCurrency(),
                    operation.getOperationAmount());
            fundsOperationDAO.save(operation);
            LOGGER.info(String.format("WITHDRAW: Account[%s] Amount[%.2f %s] New balance[%.2f]", operation.getFromAccountNumber(),
                    operation.getOperationAmount(), operation.getFromCurrency().getCurrencyCode(), newBalance));
        } catch (MissingAccountException | NotEnoughFundsException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    public void transferFunds(FundsOperation operation) {
        boolean withdrawCompleted = false;
        boolean depositCompleted = false;

        try {
            double exchangeRate = exchangeService.getExchangeRate(
                    operation.getFromCurrency().getCurrencyCode(), operation.getToCurrency().getCurrencyCode());

            Thread.sleep(10); // Simulate exchange rate service delay

            operation.setCurrencyExchangeRate(exchangeRate);

            findAccount(operation.getFromAccountNumber()).subtractBalance(operation.getFromCurrency(),
                    operation.getOperationAmount());
            withdrawCompleted = true;

            var exchangeResultingAmount = operation.getOperationAmount().multiply(BigDecimal.valueOf(exchangeRate));
            operation.setExchangeResultingAmount(exchangeResultingAmount);

            findAccount(operation.getToAccountNumber()).addBalance(operation.getToCurrency(),
                    operation.getExchangeResultingAmount());
            depositCompleted = true;

            fundsOperationDAO.save(operation);

            LOGGER.info(String.format("TRANSFER: Account[%s] Amount[%.2f %s] to Account[%s] Amount[%.2f %s]",
                    operation.getFromAccountNumber(), operation.getOperationAmount(), operation.getFromCurrency().getCurrencyCode(),
                    operation.getToAccountNumber(), operation.getExchangeResultingAmount(), operation.getToCurrency().getCurrencyCode()));

        } catch (MissingAccountException | ExchangeRateNotFoundException | NotEnoughFundsException e) {
            LOGGER.warning(e.getMessage());
        } catch (Exception e) {
            LOGGER.severe("TRANSFER: Unexpected error "+ e.getMessage());
        } finally {
            if (withdrawCompleted && !depositCompleted) {
                // Rollback
                try {
                    findAccount(operation.getFromAccountNumber()).addBalance(operation.getFromCurrency(),
                            operation.getOperationAmount());
                    LOGGER.info(String.format("TRANSFER: Rollback for Account[%s] Amount[%.2f %s]",
                            operation.getFromAccountNumber(), operation.getOperationAmount(), operation.getFromCurrency().getCurrencyCode()));
                } catch (MissingAccountException ignored) {
                }
            }
        }
    }

    public void setBenchmarkMode() {
        accountDAO.removeAllAccounts();
        accountDAO.disableSaving();
        fundsOperationDAO.disableSaving();
    }
}
