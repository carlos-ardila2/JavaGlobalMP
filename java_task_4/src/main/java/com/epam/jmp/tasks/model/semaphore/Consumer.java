package com.epam.jmp.tasks.model.semaphore;

import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Consumer extends Thread {

    private final Queue<Integer> buffer;
    private final Semaphore mutex;
    private final Semaphore emptySlots;
    private final Semaphore fullSlots;

    public Consumer(Queue<Integer>  buffer, Semaphore mutex, Semaphore emptySlots, Semaphore fullSlots) {
        this.buffer = buffer;
        this.mutex = mutex;
        this.emptySlots = emptySlots;
        this.fullSlots = fullSlots;
    }

    @Override
    public void run() {
        try {
            while (true) {
                fullSlots.acquire();
                mutex.acquire();
                int item = buffer.poll();
                System.out.println("Consumed: " + item);
                mutex.release();
                emptySlots.release();
                Thread.sleep(150); // Simulate time taken to consume an item
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
