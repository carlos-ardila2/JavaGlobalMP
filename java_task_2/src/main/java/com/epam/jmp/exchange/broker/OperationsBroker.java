package com.epam.jmp.exchange.broker;

import com.epam.jmp.exchange.model.FundsOperation;
import com.epam.jmp.exchange.service.AccountService;

import java.util.concurrent.*;
import java.util.logging.Logger;

public class OperationsBroker {

    private final AccountService accountService;
    private final BlockingQueue<FundsOperation> operationsQueue = new LinkedBlockingDeque<>(100);
    private ExecutorService executor;
    private boolean running = false;

    private static final Logger LOGGER = Logger.getLogger(OperationsBroker.class.getName());

    public OperationsBroker(AccountService accountService) {
        this.accountService = accountService;
    }

    public void start() {
        executor = Executors.newFixedThreadPool(10);

        new Thread( () -> {
            this.running = true;
            while (running) {
                FundsOperation operation = operationsQueue.poll();
                if (operation != null) {
                    switch (operation.getType()) {
                        case DEPOSIT:
                            executor.execute(() -> accountService.depositFunds(operation));
                            break;
                        case WITHDRAW:
                            executor.execute(() -> accountService.withdrawFunds(operation));
                            break;
                        case TRANSFER:
                            executor.execute(() -> accountService.transferFunds(operation));
                            break;
                    }
                }
            }
        }).start();
    }

    public void stopAfterOperationsCompleted(int timeoutSeconds) throws InterruptedException {
        if (executor != null) {
            executor.shutdown();
            boolean completed = executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);
            if (!completed) {
                LOGGER.warning("Some tasks did not complete in time!");
            }
            stop();
        }
    }

    public void stop() {
        LOGGER.info("Broker stopping...");
        if (running) {
            this.running = false;
            if (executor != null && !executor.isShutdown()) {
                LOGGER.warning("Broker shutting down executor...");
                executor.shutdownNow();
            }
            LOGGER.info("Broker stopped!");
        } else {
            LOGGER.warning("Broker already stopped!");
        }
    }

    public void addOperation(FundsOperation operation) {
        try {
            operationsQueue.put(operation);
        } catch (InterruptedException e) {
            LOGGER.warning("Broker interrupted!");
            Thread.currentThread().interrupt();
        }
    }
}
