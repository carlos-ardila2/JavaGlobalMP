package com.epam.jmp;


import com.epam.jmp.tasks.DirectoryScannerTask;
import com.epam.jmp.tasks.FactorialTask;
import com.epam.jmp.tasks.ForkBlurTask;
import com.epam.jmp.tasks.MergeSortTask;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
                case 4 -> {

                }
                case 5 -> {

                }
                case 6 -> {

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

        System.out.println("Image blur took " + (endTime - startTime) +
                " milliseconds.");

        BufferedImage dstImage =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        dstImage.setRGB(0, 0, w, h, dst, 0, w);

        return dstImage;
    }
}