package com.epam.jmp.tasks;

import java.util.concurrent.RecursiveTask;

public class FibonacciTask extends RecursiveTask<Long> {

    // Threshold for switching to linear calculation
    private static final long THRESHOLD = 10L;

    private final long number;

    public FibonacciTask(long number) {
        this.number = number;
    }

    @Override
    protected Long compute() {
        if (number <= THRESHOLD) {
            return linearCalculation(number);
        }

        FibonacciTask oneTask = new FibonacciTask(number - 1);
        oneTask.fork();
        FibonacciTask twoTask = new FibonacciTask(number - 2);

        return twoTask.compute() + oneTask.join();
    }

    private Long linearCalculation(long number) {
        if (number <= 1) {
            return number;
        }

        long previous = 0;
        long next = 1;
        long result = 0;

        for (long i = 1; i < number; i++) {
            result = previous + next;
            previous = next;
            next = result;
        }

        return result;
    }
}
