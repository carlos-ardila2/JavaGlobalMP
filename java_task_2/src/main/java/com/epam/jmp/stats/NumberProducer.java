package com.epam.jmp.stats;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class NumberProducer implements Runnable {

    private final Queue<Double> queue;
    private boolean completed = false;
    private int totalOps = 0;
    private final boolean useSync;

    public NumberProducer(Queue<Double> queue, boolean useSync) {
        this.queue = queue;
        this.useSync = useSync;
    }

    @Override
    public void run() {
        try {
            while (!completed) {
                if (useSync) {
                    synchronized (queue) {
                        queue.add(Math.random() * 10);
                    }
                } else {
                    ((BlockingQueue<Double>) queue).put(ThreadLocalRandom.current().nextDouble(10));
                }
                totalOps++;
            }
            System.out.println("Producer done!");
        } catch (InterruptedException e) {
            System.out.println("Producer interrupted!");
        }
    }

    public void stop() {
        System.out.println("Producer stopping...");
        completed = true;
    }

    public int getTotalOps() {
        return totalOps;
    }
}
