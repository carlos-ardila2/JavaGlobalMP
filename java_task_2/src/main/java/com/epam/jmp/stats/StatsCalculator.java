package com.epam.jmp.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class StatsCalculator implements Runnable, Callable<Stats> {

    private final Queue<Double> queue;
    private int totalOps = 0;
    private final boolean useSync;
    private Stats result;

    public StatsCalculator(Queue<Double> queue, boolean useSync) {
        this.queue = queue;
        this.useSync = useSync;
    }

    public StatsCalculator(Queue<Double> queue, Stats result) {
        this.queue = queue;
        this.useSync = true;
        this.result = result;
    }

    @Override
    public Stats call() {
        if (useSync) {
            synchronized (queue) {
                return calculateQueue();
            }
        } else {
            return calculateConcurrentQueue();
        }
    }

    @Override
    public void run() {
        if (result != null) {
            var stats = call();
            result.setSum(stats.getSum());
            result.setAvg(stats.getAvg());
            result.setMax(stats.getMax());
            result.setMin(stats.getMin());
        }
    }

    private Stats calculateQueue() {
        double total = 0.0;
        double max = 0.0;
        double min = Double.MAX_VALUE;
        double value;

        int totalElements = queue.size();

        while (!queue.isEmpty()) {
            value = queue.poll();

            if (value > max) {
                max = value;
            }

            if (value < min) {
                min = value;
            }

            total += value;

            totalOps++;
        }

        return new Stats(total, total / totalElements, max, min);
    }

    private Stats calculateConcurrentQueue() {
        List<Double> values = new ArrayList<>();
        ((BlockingQueue<Double>) queue).drainTo(values);

        double total = 0.0;
        double max = 0.0;
        double min = Double.MAX_VALUE;

        int totalElements = values.size();

        for(Double value : values) {
            if (value > max) {
                max = value;
            }

            if (value < min) {
                min = value;
            }

            total += value;
            totalOps++;
        }

        return new Stats(total, total / totalElements, max, min);
    }

    public int getTotalOps() {
        return this.totalOps;
    }
}
