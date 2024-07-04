package com.epam.jmp.exchange.dao;

import com.epam.jmp.exchange.model.FundsOperation;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

import static com.epam.jmp.exchange.model.FundsOperation.Type.DEPOSIT;
import static com.epam.jmp.exchange.model.FundsOperation.Type.TRANSFER;
import static java.math.RoundingMode.DOWN;
import static java.nio.file.StandardOpenOption.*;

public class FundsOperationDAO {

    private static final Logger LOGGER = Logger.getLogger(FundsOperationDAO.class.getName());

    private final LinkedHashSet<FundsOperation> operations = new LinkedHashSet<>();

    private boolean savingEnabled = true;

    public FundsOperationDAO() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveOperationsData));
    }

    public void save(FundsOperation operation) {
        if (savingEnabled) {
            operation.setOperationDate(LocalDateTime.now());
            operations.add(operation);
        }
    }

    private void saveOperationsData() {
        if (savingEnabled) {
            var path = Path.of("funds_operations.txt");
            try (BufferedWriter reader = Files.newBufferedWriter(path, CREATE, WRITE, APPEND)) {
                for(var operation : operations) {
                    var type = operation.getType();
                    reader.write(String.format("[%s] %s: %s %s %.2f%s%s%s%s",
                            operation.getOperationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            type.name(),
                            type.equals(DEPOSIT) ? "To: " + operation.getToAccountNumber() : "From: " + operation.getFromAccountNumber(),
                            type.equals(DEPOSIT) ? operation.getToCurrency().getCurrencyCode() : operation.getFromCurrency().getCurrencyCode(),
                            operation.getOperationAmount(),
                            type.equals(TRANSFER) ? " To: " + operation.getToAccountNumber() : "",
                            type.equals(TRANSFER) ? " " + operation.getToCurrency().getCurrencyCode() : "",
                            type.equals(TRANSFER) ? " " + operation.getExchangeResultingAmount().setScale(2, DOWN).toPlainString() : "",
                            type.equals(TRANSFER) ? "; Rate: " + operation.getCurrencyExchangeRate() : ""));
                    reader.newLine();
                }
            } catch (Exception e) {
                LOGGER.severe("Failed to save funds operations data: " + e.getMessage());
            }
        }
    }

    public void disableSaving() {
        this.savingEnabled = false;
    }
}
