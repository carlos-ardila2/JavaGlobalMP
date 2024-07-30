package com.epam.jmp.tasks.model.blockingqueue;

import java.util.concurrent.BlockingQueue;

public class BlockingConsumer extends Thread {

    private final BlockingQueue<Integer> buffer;

    public BlockingConsumer(BlockingQueue<Integer>  buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int item = buffer.take();
                System.out.println("Consumed: " + item);
                Thread.sleep(150); // Simulate time taken to consume an item
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
