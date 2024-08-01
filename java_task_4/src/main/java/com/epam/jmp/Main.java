package com.epam.jmp;

import com.epam.jmp.akka.service.PCAssemblyService;
import com.epam.jmp.opensalary.model.Employee;
import com.epam.jmp.opensalary.service.EmployeeService;
import com.epam.jmp.opensalary.service.PayRollService;
import com.epam.jmp.tasks.*;
import com.epam.jmp.tasks.model.blockingqueue.BlockingConsumer;
import com.epam.jmp.tasks.model.blockingqueue.BlockingProducer;
import com.epam.jmp.tasks.model.semaphore.Consumer;
import com.epam.jmp.tasks.model.semaphore.Producer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {

        new Main().runApp();
    }

    public void runApp() {
        String tests = """
                Select the experiment to run:
                    1. Factorial via FJP
                    2. Multithreading Sorting via FJP
                    3. File (Directory) Scanner via FJP
                    41. Completable Future Helps to Build Open Salary Society
                    42. Completable Future Helps to Build Open Salary Society (Async)
                    51. Producer–Consumer Problem (Semaphores)
                    52. Producer–Consumer Problem (BlockingQueue)
                    6. Recursive Action
                    7. Blurring for Clarity
                    8. PC Assembly Line with Akka
                :\s""";

        System.out.print(tests);
        try (Scanner console = new Scanner(System.in)) {
            var selection = console.nextInt();

            switch (selection) {
                case 1 -> {
                    System.out.println("Enter a number to calculate factorial: ");
                    var number = console.nextInt();
                    BigInteger result;
                    try (ForkJoinPool pool = new ForkJoinPool()) {
                        FactorialTask task = new FactorialTask(1, number);
                        result = pool.invoke(task);
                    }

                    System.out.println("Factorial of " + number + " is " + result);
                }
                case 2 -> {
                    System.out.println("Enter the length of the array. It will be filled with random integers: ");
                    var size = console.nextInt();

                    int[] array = new int[size]; // Random array
                    for (int i = 0; i < size; i++) {
                        array[i] = (int) (Math.random() * 1000);
                    }

                    try (ForkJoinPool pool = new ForkJoinPool()) {
                        MergeSortTask task = new MergeSortTask(array);
                        pool.invoke(task);
                    }

                    // Now the array should be sorted
                    System.out.println(Arrays.toString(array));
                }
                case 3 -> {
                    System.out.println("Enter a path in your file system: ");
                    var path = console.next();

                    Path directoryPath = Path.of(path);
                    if (Files.isDirectory(directoryPath)) {
                        try (ForkJoinPool pool = new ForkJoinPool()) {
                            DirectoryScannerTask task = new DirectoryScannerTask(directoryPath);
                            var result = pool.invoke(task);
                            System.out.println(path + " statistics:");
                            System.out.printf("Directory Count: %d, File Count: %d, Total Size: %s",
                                    result.getDirectoryCount(), result.getFileCount(), getFormatedSize(result.getTotalSize()));
                        }
                    } else {
                        System.out.println("ERROR: The path is not a directory.");
                    }
                }
                case 41 -> {
                    long startTime = System.currentTimeMillis();
                    PayRollService service = new PayRollService(new EmployeeService());
                    List<Employee> employees = service.fetchHiredEmployeesWithSalaries();
                    employees.forEach(System.out::println);
                    long endTime = System.currentTimeMillis();
                    System.out.println("Synchronous Payroll update took " + (endTime - startTime) + " milliseconds.");
                }
                case 42 -> {
                    long startTime = System.currentTimeMillis();
                    PayRollService service = new PayRollService(new EmployeeService());
                    CompletableFuture<List<Employee>> future = service.fetchHiredEmployeesWithSalariesAsync().toCompletableFuture();
                    future.thenAccept(employees -> employees.forEach(System.out::println)).join(); // Wait for completion
                    long endTime = System.currentTimeMillis();
                    System.out.println("Async Payroll update took " + (endTime - startTime) + " milliseconds.");

                    // Synchronous Payroll update took 5538 milliseconds.
                    // Async Payroll update took 1689 milliseconds.
                }
                case 51 -> {
                    // Producer–Consumer problem with semaphores
                    final int BUFFER_SIZE = 10;
                    final Queue<Integer> buffer = new LinkedList<>();
                    final Semaphore emptySlots = new Semaphore(BUFFER_SIZE);
                    final Semaphore fullSlots = new Semaphore(0);
                    final Semaphore mutex = new Semaphore(1);
                    Producer producer = new Producer(buffer, mutex, emptySlots, fullSlots);
                    Consumer consumer = new Consumer(buffer, mutex, emptySlots, fullSlots);
                    producer.start();
                    consumer.start();

                    System.out.println("Enter any key to stop the simulation.");
                }
                case 52 -> {
                    // Blocking queue solves the problem of busy waiting directly
                    final int BUFFER_SIZE = 10;
                    final BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(BUFFER_SIZE);
                    BlockingProducer producer = new BlockingProducer(buffer);
                    BlockingConsumer consumer = new BlockingConsumer(buffer);
                    producer.start();
                    consumer.start();
                }
               case 6 -> {
                    System.out.println("Enter a number to calculate Fibonacci series: ");
                    var number = console.nextInt();
                    long result;
                    try (ForkJoinPool pool = new ForkJoinPool()) {
                        FibonacciTask task = new FibonacciTask(number);
                        result = pool.invoke(task);
                    }

                    System.out.println("Number " + number + " of Fibonacci series is " + result);

                    System.out.println("\nNOTE: Check the test output for performance comparison on the rest of the task.");
                    
                    /*
                    Benchmark                                      Mode  Cnt    Score    Error  Units
                    BenchmarkTest.doubleSquaresLinearBenchmark     avgt    5  484.758 ± 91.251  ms/op
                    BenchmarkTest.doubleSquaresRecursiveBenchmark  avgt    5   78.249 ±  2.475  ms/op
                    */
                }
                case 7 -> {
                    System.out.println("Enter the path of a jpg image in your file system: ");
                    var path = console.next();

                    Path filePath = Path.of(path);
                    if (Files.isRegularFile(filePath)) {
                        try {
                            BufferedImage image = ImageIO.read(filePath.toFile());
                            System.out.println("Source image: " + path);

                            BufferedImage blurredImage = blurImage(image);

                            String dstName = filePath.getFileName().toString();
                            dstName = dstName.substring(0, dstName.lastIndexOf('.')) + "-blurred.jpg";
                            File dstFile = new File(filePath.getParent().toFile(), dstName);
                            boolean success = ImageIO.write(blurredImage, "jpg", dstFile);

                            if (!success) throw new IOException();

                            System.out.println("Output image: " + dstFile.getAbsolutePath());
                        } catch (IOException e) {
                            System.out.println("ERROR: Can't read or write the image file.");
                        }
                    } else {
                        System.out.println("ERROR: The path is not a file.");
                    }
                }
                case 8 -> {
                    System.out.println("Enter the number PC's to assemble: ");
                    var number = console.nextInt();

                    PCAssemblyService service = new PCAssemblyService();

                    var results = new CompletableFuture[number];

                    for (int i = 0; i < number; i++) {
                        results[i] = service.assemblePC();
                    }

                    Stream.of(results)
                            .map(CompletableFuture::join)
                            .forEach(pc -> System.out.println("PC Assembled: " + pc));

                    service.shutdown();
                }
            }
        }
    }

    private static String getFormatedSize(double size) {
        String formatedSize;
        if (size < 1000) {
            formatedSize = String.format("%,.2f bytes", size);
        } else if (size < 1000000) {
            formatedSize = String.format("%,.2f Kb", size / 1000);
        } else if (size < 1000000000) {
            formatedSize = String.format("%,.2f Mb", size / (1000 * 1024));
        } else {
            formatedSize = String.format("%,.2f Gb", size / (1000 * 1024 * 1024));
        }
        return formatedSize;
    }

    public static BufferedImage blurImage(BufferedImage srcImage) {
        int w = srcImage.getWidth();
        int h = srcImage.getHeight();

        int[] src = srcImage.getRGB(0, 0, w, h, null, 0, w);
        int[] dst = new int[src.length];

        System.out.println("Array size is " + src.length);
        System.out.println("Threshold is " + 10000);

        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println(processors + " processor"
                + (processors != 1 ? "s are " : " is ")
                + "available");

        ForkBlurTask fb = new ForkBlurTask(src, 0, src.length, dst);

        long startTime;
        try (ForkJoinPool pool = new ForkJoinPool()) {

            startTime = System.currentTimeMillis();
            pool.invoke(fb);
        }
        long endTime = System.currentTimeMillis();

        System.out.println("Image blur took " + (endTime - startTime) + " milliseconds.");

        BufferedImage dstImage =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        dstImage.setRGB(0, 0, w, h, dst, 0, w);

        return dstImage;
    }
}