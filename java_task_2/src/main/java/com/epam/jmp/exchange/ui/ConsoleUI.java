package com.epam.jmp.exchange.ui;

import com.epam.jmp.PerformanceMeasurable;
import com.epam.jmp.exchange.broker.OperationsBroker;
import com.epam.jmp.exchange.exception.MissingAccountException;
import com.epam.jmp.exchange.model.Account;
import com.epam.jmp.exchange.model.FundsOperation;
import com.epam.jmp.exchange.service.AccountService;
import com.epam.jmp.exchange.service.CurrencyExchangeService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    private final PerformanceMeasurable performance;
    private final AccountService accountService;
    private final CurrencyExchangeService exchangeService;
    private final OperationsBroker broker;

    public ConsoleUI(PerformanceMeasurable performance, AccountService accountService,
                     CurrencyExchangeService exchangeService, OperationsBroker broker) {
        this.performance = performance;
        this.accountService = accountService;
        this.exchangeService = exchangeService;
        this.broker = broker;
    }

    public void start(Scanner console) {
        if (broker != null) {
            broker.start();
        }

        var selection = 0;

        String greeting = """
                Welcome to the Currency Exchange Service!
                Accounts can hold and exchange funds in multiple currencies.
                """;
        System.out.println(greeting);

        String menu = """
                Select a task to run:
                    1. Create Account
                    2. Deposit Funds
                    3. Withdraw Funds
                    4. Transfer Funds (with currency exchange)
                    5. View Account Balance
                    6. Update Exchange Rate
                    7. Benchmark Service (Random Transactions)
                    8. Exit
                :\s""";

        console.useDelimiter("\\R");

        while (selection != 8) {
            System.out.print(menu);
            selection = console.nextInt();

            switch (selection) {
                case 1 -> {
                    System.out.print("Enter account holder name (empty to cancel): ");
                    var name = console.next();
                    if (name.isEmpty()) break;

                    var newAccount = accountService.createAccount(name);
                    System.out.println("New account created! Account number: " + newAccount.getAccountNumber());

                    var optionalDepositOperation = new FundsOperation(FundsOperation.Type.DEPOSIT);
                    optionalDepositOperation.setToAccountNumber(newAccount.getAccountNumber());

                    var initialBalanceProvided = captureFundsOperationData(console, optionalDepositOperation,
                            "initial deposit", "initial amount", false);

                    if (initialBalanceProvided) {
                        accountService.depositFunds(optionalDepositOperation);
                    }
                }
                case 2 -> {
                    var account = findAccount(console, "");
                    if (account == null) break;

                    var depositOperation = new FundsOperation(FundsOperation.Type.DEPOSIT);
                    depositOperation.setToAccountNumber(account.getAccountNumber());

                    if (captureFundsOperationData(console, depositOperation, "deposit", "deposit amount", false)) {
                        if (broker != null) {
                            broker.addOperation(depositOperation);
                        } else {
                            // No broker, execute directly
                            accountService.depositFunds(depositOperation);
                        }
                    }
                }
                case 3 -> {
                    var account = findAccount(console, "");
                    if (account == null) break;

                    var withdrawOperation = new FundsOperation(FundsOperation.Type.WITHDRAW);
                    withdrawOperation.setFromAccountNumber(account.getAccountNumber());

                    if (captureFundsOperationData(console, withdrawOperation, "withdraw", "withdraw amount", true)) {
                        if (broker != null) {
                            broker.addOperation(withdrawOperation);
                        } else {
                            accountService.withdrawFunds(withdrawOperation);
                        }
                    }
                }
                case 4 -> {
                    var fromAccount = findAccount(console, "from");
                    if (fromAccount == null) break;

                    var transferOperation = new FundsOperation(FundsOperation.Type.TRANSFER);
                    transferOperation.setFromAccountNumber(fromAccount.getAccountNumber());

                    if (captureFundsOperationData(console, transferOperation, "transfer from", "transfer amount", true)) {
                        var toAccount = findAccount(console, "to");
                        if (toAccount == null) break;

                        transferOperation.setToAccountNumber(toAccount.getAccountNumber());

                        if (captureFundsOperationData(console, transferOperation, "transfer to", "transfer amount", false)) {
                            if (broker != null) {
                                broker.addOperation(transferOperation);
                            } else {
                                accountService.transferFunds(transferOperation);
                            }
                        }
                    }
                }
                case 5 -> {
                    var account = findAccount(console, "");
                    if (account == null) break;
                    printAccountBalance(account);
                }
                case 6 -> {
                    var dummyOperation = new FundsOperation(FundsOperation.Type.TRANSFER);
                    dummyOperation.setOperationAmount(BigDecimal.ONE);

                    if (captureFundsOperationData(console, dummyOperation, "from", "", true)) {
                        dummyOperation.setOperationAmount(null);
                        if (captureFundsOperationData(console, dummyOperation, "to", "exchange rate", false)) {
                            var exchangeRate = dummyOperation.getOperationAmount().doubleValue();
                            var fromCurrency = dummyOperation.getFromCurrency().getCurrencyCode();
                            var toCurrency = dummyOperation.getToCurrency().getCurrencyCode();
                            exchangeService.setExchangeRate(fromCurrency, toCurrency, exchangeRate);
                            System.out.println("Exchange rate updated!");
                        }
                    }
                }
                case 7 -> {
                    System.out.print("Number of operations to run: ");
                    var operationsToRun = console.nextInt();

                    var timeout = 0;
                    if (broker != null) {
                        System.out.print("Time to run the benchmark (seconds): ");
                        timeout = console.nextInt();
                    }

                    List<Currency> currencies = List.of(Currency.getInstance("USD"),
                            Currency.getInstance("EUR"),
                            Currency.getInstance("CAD"),
                            Currency.getInstance("GBP"));

                    List<String> accountNumbers = new ArrayList<>();

                    // Clear accounts and stop saving operations before benchmark
                    accountService.setBenchmarkMode();

                    for (var i = 0; i < 10; i++) {
                        var account = accountService.createAccount("Account " + i);
                        for(var currency : currencies) {
                            account.addBalance(currency, BigDecimal.valueOf(1000));
                        }
                        accountNumbers.add(account.getAccountNumber());
                    }

                    var random = new java.util.Random();

                    performance.startPerformanceMeasurement();

                    System.out.println("Running Benchmark ...");

                    for (var i = 0; i < operationsToRun; i++) {
                        var fromAccount = accountNumbers.get(random.nextInt(accountNumbers.size()));
                        var toAccount = accountNumbers.get(random.nextInt(accountNumbers.size()));
                        var fromCurrency = currencies.get(random.nextInt(currencies.size()));
                        var toCurrency = currencies.get(random.nextInt(currencies.size()));
                        var amount = BigDecimal.valueOf(random.nextDouble(10));
                        var operation = new FundsOperation(FundsOperation.Type.TRANSFER);
                        operation.setFromAccountNumber(fromAccount);
                        operation.setToAccountNumber(toAccount);
                        operation.setFromCurrency(fromCurrency);
                        operation.setToCurrency(toCurrency);
                        operation.setOperationAmount(amount);

                        if (broker != null) {
                            broker.addOperation(operation);
                        } else {
                            accountService.transferFunds(operation);
                        }
                    }
                    performance.addToOperationsCount(operationsToRun);

                    try {
                        if (broker != null) {
                            broker.stopAfterOperationsCompleted(timeout);
                        }
                    } catch (InterruptedException e) {
                        System.out.println("WARN: " + e.getMessage());
                    }

                    try {
                        for (String number : accountNumbers) {
                            var account = accountService.findAccount(number);
                            printAccountBalance(account);
                        }
                    } catch (MissingAccountException e) {
                        System.out.println(e.getMessage());
                    }

                    selection = 8; // Exit after benchmark
                }
                case 8 -> {
                    if (broker != null) {
                        broker.stop();
                    }
                    System.out.println("Thank you come back soon!");
                }
                default -> System.out.println("Invalid selection!");
            }
            System.out.println("\n-------------------------------\n");
        }
    }

    private Account findAccount(Scanner console, String message) {
        boolean validAccount = false;
        Account account = null;

        do {
            System.out.print("Enter " + message + " account number (empty to cancel): ");
            var id = console.next();
            if (id.isEmpty()) return null;

            try {
                account = accountService.findAccount(id);
                validAccount = true;
            } catch (MissingAccountException e) {
                System.out.println(e.getMessage());
            }
        } while (!validAccount);

        return account;
    }

    private void printAccountBalance(Account account) {
        System.out.println("\n" + account.getCustomerName() + "'s account balance:");
        for (var currency : account.getAvailableCurrencies()) {
            System.out.println(currency + ": " + account.getBalance(currency));
        }
    }

    private boolean captureFundsOperationData(Scanner console, FundsOperation operation, String currencyMessage,
                                              String numberMessage, boolean from) {

        boolean validCurrency = false;
        Currency currency = null;

        do {
            System.out.print("Enter " + currencyMessage + " currency [USD, EUR, CAD, GBP, COP, etc..] (empty to cancel): ");
            var optionalCurrency = console.next();
            if (optionalCurrency.isEmpty()) return false;

            try {
                currency = Currency.getInstance(optionalCurrency);
                validCurrency = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid currency!");
            }
        } while (!validCurrency);

        BigDecimal amount = null;

        if (currency != null && operation.getOperationAmount() == null) {
            boolean validAmount = false;
            do {
                System.out.print("Enter " + numberMessage + " (empty to cancel): ");
                var optionalAmount = console.next();
                if (optionalAmount.isEmpty()) return false;

                try {
                    amount = new BigDecimal(optionalAmount);
                    validAmount = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid " + numberMessage + "!");
                }
            } while (!validAmount);
        }

        if (currency != null) {
            if (from) {
                operation.setFromCurrency(currency);
            } else {
                operation.setToCurrency(currency);
            }
        }

        if (amount != null) {
            operation.setOperationAmount(amount);
        }

        return true;
    }
}
