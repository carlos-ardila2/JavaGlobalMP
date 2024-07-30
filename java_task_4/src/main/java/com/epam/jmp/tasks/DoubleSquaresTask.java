package com.epam.jmp.tasks;

import java.util.concurrent.RecursiveTask;

public class DoubleSquaresTask extends RecursiveTask<Double> {

        // Threshold for switching to linear calculation
        private static final int THRESHOLD = 1000;

        private final double[] array;
        private final int start;
        private final int end;

        public DoubleSquaresTask(double[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Double compute() {
            if (end - start <= THRESHOLD) {
                return linearCalculation(start, end);
            }

            int mid = (start + end) / 2;

            DoubleSquaresTask leftTask = new DoubleSquaresTask(array, start, mid);
            DoubleSquaresTask rightTask = new DoubleSquaresTask(array, mid, end);

            leftTask.fork();
            double rightResult = rightTask.compute();
            double leftResult = leftTask.join();

            return leftResult + rightResult;
        }

        private double linearCalculation(int start, int end) {
            double result = 0;
            for (int i = start; i < end; i++) {
                result += array[i] * array[i];
            }
            return result;
        }
}
