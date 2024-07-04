package com.epam.jmp;


import com.epam.jmp.tasks.DirectoryScannerTask;
import com.epam.jmp.tasks.FactorialTask;
import com.epam.jmp.tasks.MergeSortTask;
import com.epam.jmp.tasks.model.DirectoryStatistics;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

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
                    4. Completable Future Helps to Build Open Salary Society
                    5. Producer–Consumer Problem
                    6. RecursiveAction
                    7. Blurring for Clarity
                    8. PC Assembly Line with Akka
                :\s""";

        System.out.print(tests);
        Scanner console = new Scanner(System.in);
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
            case 4 -> {

            }
            case 5 -> {

            }
            case 6 -> {

            }
            case 7 -> {

            }
            case 8 -> {

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
}