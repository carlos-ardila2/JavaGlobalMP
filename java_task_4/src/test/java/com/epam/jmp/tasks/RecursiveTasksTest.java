package com.epam.jmp.tasks;

import org.testng.annotations.Test;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test()
public class RecursiveTasksTest {

    private static final int SIZE = 500_000_000;

    @Test()
    public void fibonacciTaskTest() {
        try (ForkJoinPool pool = new ForkJoinPool()) {
            assertEquals(1134903170L, pool.invoke(new FibonacciTask(45L)));
        }
    }

    @Test()
    public void doubleSquaresLinearTest() {

        double[] array = generateRandomArray();

        long startTime = System.currentTimeMillis();

        double sum = 0;
        for (double v : array) {
            sum += v * v;
        }

        long endTime = System.currentTimeMillis();

        System.out.println("LINEAR: Result: " + sum + " Task took " + (endTime - startTime) + " milliseconds.");

        assertTrue(sum > 0);
    }

    @Test()
    public void doubleSquaresRecursiveTest() {

        double[] array = generateRandomArray();

        long startTime = System.currentTimeMillis();

        try (ForkJoinPool pool = new ForkJoinPool()) {
            double sum = pool.invoke(new DoubleSquaresTask(array, 0, array.length));
            long endTime = System.currentTimeMillis();

            System.out.println("RECURSIVE: Result: " + sum + " Task took " + (endTime - startTime) + " milliseconds.");

            assertTrue(sum > 0);
        }
    }

    private static double[] generateRandomArray() {
        Random random = new Random();
        double[] array = new double[SIZE];
        for (int i = 0; i < SIZE; i++) {
            array[i] = random.nextDouble(10000);
        }
        return array;
    }
}
