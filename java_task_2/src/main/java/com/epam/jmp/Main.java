package com.epam.jmp;

import com.epam.jmp.bus.Consumer;
import com.epam.jmp.bus.MessageBus;
import com.epam.jmp.bus.Producer;
import com.epam.jmp.exchange.broker.OperationsBroker;
import com.epam.jmp.exchange.dao.AccountDAO;
import com.epam.jmp.exchange.dao.FundsOperationDAO;
import com.epam.jmp.exchange.service.AccountService;
import com.epam.jmp.exchange.service.CurrencyExchangeService;
import com.epam.jmp.exchange.ui.ConsoleUI;
import com.epam.jmp.pool.PoolUser;
import com.epam.jmp.pool.BlockingObjectPoolImpl;
import com.epam.jmp.stats.NumberProducer;
import com.epam.jmp.stats.Stats;
import com.epam.jmp.stats.StatsCalculator;
import com.epam.jmp.threads.ReadThread;
import com.epam.jmp.threads.WriteThread;

import java.util.*;
import java.util.concurrent.*;

public class Main implements PerformanceMeasurable {

    private static long totalOps = 0L;
    private static long startTime;

    public static void main(String[] args) {
        new Main().runApp();
    }

    public void runApp() {
        String tests = """
                Select the experiment to run:
                    1. Das Experiment
                    2. Deadlocks
                    3. Where’s Your Bus, Dude?
                    4. Task 4 (Blocking pool)
                    5. Task 5 (Currency Exchange Service)
                    6. Task 5 (Currency Exchange Service with Broker)
                    7. Task 6 (Consumer Producer Classic)
                    8. Task 6 (Consumer Producer with BlockingQueue)
                :\s""";

        System.out.print(tests);
        Scanner console = new Scanner(System.in);
        var selection = console.nextInt();

        switch (selection) {
            case 1 -> {
                /*
                 * This never causes any ConcurrentModificationException.
                 */
                System.out.println("Starting Threads Experiment");
                Map<Integer, Integer> plainMap = new HashMap<>();
                ConcurrentHashMap<Integer, Integer> concurrentMap = new ConcurrentHashMap<>(plainMap);
                try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
                    executor.submit(new ReadThread(concurrentMap));
                    executor.submit(new WriteThread(concurrentMap));
                    executor.shutdown();
                    System.out.println("stand by...");
                    try {
                        boolean completed = executor.awaitTermination(1, TimeUnit.MINUTES);
                        System.out.println((completed ? "Finished :)" : "Timed out!"));
                    } catch (InterruptedException ie) {
                        System.out.println("Failure: " + ie.getMessage());
                    } catch (Exception e) {
                        System.out.println("Big Failure: " + e.getMessage());
                    }
                }
            }
            case 2 -> {
                System.out.println("Starting Deadlocks Experiment");
                List<Double> collection = new ArrayList<>();

                Thread t1 = new Thread(() -> {
                    for (int i = 0; i < 10_000; i++) {
                        // Lock to write the value
                        var value = Math.random() * i;
                        synchronized (collection) {
                            collection.add(value);
                            System.out.println("Pushed: " + collection.get(i));
                        }
                    }
                });
                t1.start();

                Thread t2 = new Thread(() -> {
                    while (collection.size() < 10_000) {
                        double sum = 0;
                        // Lock to compute the sum
                        synchronized (collection) {
                            for (Double value : collection) {
                                sum += value;
                            }
                        }
                        System.out.println("Sum of values " + sum);
                    }
                });
                t2.start();

                Thread t3 = new Thread(() -> {
                    while (collection.size() < 10_000) {
                        double sum = 0;
                        // Lock to compute the sum
                        synchronized (collection) {
                            for (Double value : collection) {
                                sum += Math.sqrt(value);
                            }
                        }
                        // Release the lock before final calculation
                        System.out.println("Square of the sum of squares " + Math.sqrt(sum));
                    }
                });
                t3.start();

                try {
                    t1.join();
                    t2.join();
                    t3.join();
                } catch (InterruptedException e) {
                    System.err.println("Failure: " + e.getMessage());
                }
            }
            case 3 -> {
                var threads = getFruitThreads();

                try {
                    for (var thread : threads) {
                        thread.join();
                    }
                } catch (InterruptedException e) {
                    System.err.println("Failure: " + e.getMessage());
                }
            }
            case 4 -> {
                BlockingObjectPoolImpl pool = new BlockingObjectPoolImpl(10);

                try (ExecutorService executor = Executors.newFixedThreadPool(6)) {

                    for (int i = 0; i < 6; i++) {
                        executor.execute(new PoolUser(pool));
                    }

                    try {
                        executor.shutdown();
                        boolean completed = executor.awaitTermination(40, TimeUnit.SECONDS);
                        System.out.println("Pool size: " + pool.size());
                        if (completed) {
                            System.out.println("All threads finished!");
                        } else {
                            System.out.println("Timed out!");
                            System.exit(-1);
                        }
                    } catch (InterruptedException ie) {
                        System.out.println("Failure: " + ie.getMessage());
                    } catch (Exception e) {
                        System.out.println("Big Failure: " + e.getMessage());
                    }
                }
            }
            case 5 -> {
                runFundsExchangeOperations(console, false);
            }
            case 6 -> {
                runFundsExchangeOperations(console, true);
            }
            case 7 -> {
                Queue<Double> queue = new LinkedList<>();
                runStatsCalculation(queue, true);
            }
            case 8 -> {
                Queue<Double> queue = new LinkedBlockingDeque<>(1000);
                runStatsCalculation(queue, false);
            }
            default -> System.out.println("Invalid selection");
        }
    }

    private static ArrayList<Thread> getFruitThreads() {
        MessageBus bus = new MessageBus();

        var fruits = List.of("apple", "banana", "orange", "pear", "grape");

        var threads = new ArrayList<Thread>();

        for (var fruit : fruits) {
            var producer = new Thread(new Producer(bus, fruit, "p" + threads.size()));
            threads.add(producer);
            var consumer = new Thread(new Consumer(bus, fruit));
            threads.add(consumer);
            producer.start();
            consumer.start();
        }
        return threads;
    }

    private void runFundsExchangeOperations(Scanner console, boolean useBroker) {
        FundsOperationDAO fundsOperationDAO = new FundsOperationDAO();
        CurrencyExchangeService currencyService = new CurrencyExchangeService();
        AccountDAO accountDAO = new AccountDAO();
        AccountService accountService = new AccountService(accountDAO, fundsOperationDAO, currencyService);
        OperationsBroker broker = useBroker ? new OperationsBroker(accountService) : null;
        new ConsoleUI(this, accountService, currencyService, broker).start(console);
    }

    private void runStatsCalculation(Queue<Double> queue, boolean useSync) {
        Stats result = null;
        boolean working = true;
        List<NumberProducer> producers = new ArrayList<>();

        startPerformanceMeasurement();

        if (useSync) {
            for (int i = 0; i < 4; i++) {
                var producer = new NumberProducer(queue, true);
                producers.add(producer);
                new Thread(producer).start();
            }

            while (working) {
                result = new Stats();
                var calculator = new StatsCalculator(queue, result);
                var calculatorThread = new Thread(calculator);
                calculatorThread.start();
                try {
                    calculatorThread.join();
                } catch (InterruptedException e) {
                    System.err.println("Failure: " + e.getMessage());
                }

                System.out.printf("Partial Results: Sum: %f, Average: %f, Minimum %f, Maximum: %f\n",
                        result.getSum(), result.getAvg(), result.getMin(), result.getMax());
                addToOperationsCount(calculator.getTotalOps());

                working = result.getSum() < 5000;
            }

            for (var producer : producers) {
                producer.stop();
                addToOperationsCount(producer.getTotalOps());
            }
        } else {
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

                for (int i = 0; i < 4; i++) {
                    var producer = new NumberProducer(queue, false);
                    producers.add(producer);
                    executor.execute(producer);
                }

                while (working) {
                    var calculator = new StatsCalculator(queue, false);
                    var calculatorResult = executor.submit((Callable<Stats>) calculator);
                    result = calculatorResult.get();
                    System.out.printf("Partial Results: Sum: %f, Average: %f, Minimum %f, Maximum: %f\n",
                            result.getSum(), result.getAvg(), result.getMin(), result.getMax());
                    addToOperationsCount(calculator.getTotalOps());

                    working = result.getSum() < 5000;
                }

                for (var producer : producers) {
                    producer.stop();
                    addToOperationsCount(producer.getTotalOps());
                }

                executor.shutdown();

                if (!executor.isTerminated()) {
                    executor.shutdownNow();
                }
            } catch (ExecutionException e) {
                System.out.println("Failure: " + e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (result != null) {
                System.out.printf("Final Results: Sum: %f, Average: %f, Minimum %f, Maximum: %f\n",
                        result.getSum(), result.getAvg(), result.getMin(), result.getMax());
            }
        }
    }

    @Override
    public void startPerformanceMeasurement() {
        startTime = System.currentTimeMillis();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopPerformanceMeasurement));
    }

    @Override
    public void stopPerformanceMeasurement() {
        System.out.printf("Total operations: %d, Elapsed time: %d ms, Operations per second: %.0f\n",
                getOperationsCount(), System.currentTimeMillis() - startTime, getOperationsCount() /
                        ((System.currentTimeMillis() - startTime) / 1000.0));
    }
    @Override
    public long getOperationsCount() {
        return totalOps;
    }

    @Override
    public void addToOperationsCount(long numberOfOperations) {
        totalOps += numberOfOperations;
    }
}