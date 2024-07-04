package com.epam.jmp.tasks;

import java.math.BigInteger;
import java.util.concurrent.RecursiveTask;

public class FactorialTask extends RecursiveTask<BigInteger> {

    // Threshold for switching to sequential calculation
    private static final int THRESHOLD = 19;

    private final int start;
    private final int end;

    public FactorialTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected BigInteger compute() {
        if (end - start <= THRESHOLD) {
            return sequentialFactorial(start, end);
        }

        int mid = (start + end) / 2;

        FactorialTask leftTask = new FactorialTask(start, mid);
        FactorialTask rightTask = new FactorialTask(mid + 1, end);

        leftTask.fork();
        BigInteger rightResult = rightTask.compute();
        BigInteger leftResult = leftTask.join();

        return leftResult.parallelMultiply(rightResult);
    }

    private BigInteger sequentialFactorial(int start, int end) {
        BigInteger result = BigInteger.ONE;
        for (int i = start; i <= end; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}
