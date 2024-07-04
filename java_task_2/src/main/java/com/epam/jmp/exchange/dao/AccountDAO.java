package com.epam.jmp.exchange.dao;

import com.epam.jmp.exchange.model.Account;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static java.nio.file.StandardOpenOption.*;

public class AccountDAO {

    private static final Logger LOGGER = Logger.getLogger(AccountDAO.class.getName());

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private boolean savingEnabled = true;

    public AccountDAO() {
        loadAccountsData();
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveAccountsData));
    }

    private void loadAccountsData() {
        var path = Path.of("accounts");
        if (Files.exists(path) && Files.isDirectory(path)) {
            try (var accountFiles = Files.list(path)) {
                accountFiles.forEach(file -> {
                    try (var objStream = new ObjectInputStream(Files.newInputStream(file, READ))) {
                        var account = (Account) objStream.readObject();
                        accounts.put(account.getAccountNumber(), account);
                    } catch (IOException | ClassNotFoundException e) {
                        LOGGER.severe("Failed to load account data: " + e.getMessage());
                    }
                });
            } catch (IOException e) {
                LOGGER.severe("Failed to load account data: " + e.getMessage());
            }
        }
    }

    public void saveAccountsData() {
        if (savingEnabled) {
            var path = Path.of("accounts");
            try {
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                } else if(!Files.isDirectory(path)) {
                    LOGGER.severe("Failed to create accounts directory: file with the same name already exists");
                    return;
                }

                for (var account : accounts.values()) {
                    var accountPath = path.resolve(account.getAccountNumber() + ".dat");
                    try (var objStream = new ObjectOutputStream(Files.newOutputStream(accountPath, CREATE, WRITE, TRUNCATE_EXISTING))) {
                        objStream.writeObject(account);
                    }
                }
            } catch (IOException e) {
                LOGGER.severe("Failed to create accounts directory: " + e.getMessage());
            }
        }
    }

    public Account findById(String id) {
        return accounts.get(id);
    }

    public Account create(String name) {

        var newId = 0;
        do {
            newId = random.nextInt(1000, 9999);
        } while (accounts.containsKey(String.valueOf(newId)));

        var account = new Account(String.valueOf(newId), name);
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    public void removeAllAccounts() {
        accounts.clear();
    }

    public void disableSaving() {
        savingEnabled = false;
    }
}
