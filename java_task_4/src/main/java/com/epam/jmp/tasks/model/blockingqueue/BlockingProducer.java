package com.epam.jmp.tasks.model.blockingqueue;

import java.util.concurrent.BlockingQueue;

public class BlockingProducer extends Thread {

    private final BlockingQueue<Integer> buffer;

    public BlockingProducer(BlockingQueue<Integer>  buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            int item = 0;
            while (true) {
                buffer.put(item);
                System.out.println("Produced: " + item);
                item++;
                Thread.sleep(100); // Simulate time taken to produce an item
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
