package com.epam.jmp.tasks.model.semaphore;

import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Producer extends Thread {

    private final Queue<Integer> buffer;
    private final Semaphore mutex;
    private final Semaphore emptySlots;
    private final Semaphore fullSlots;

    public Producer(Queue<Integer>  buffer, Semaphore mutex, Semaphore emptySlots, Semaphore fullSlots) {
        this.buffer = buffer;
        this.mutex = mutex;
        this.emptySlots = emptySlots;
        this.fullSlots = fullSlots;
    }

    @Override
    public void run() {
        try {
            int item = 0;
            while (true) {
                emptySlots.acquire();
                mutex.acquire();
                buffer.add(item);
                System.out.println("Produced: " + item);
                item++;
                mutex.release();
                fullSlots.release();
                Thread.sleep(100); // Simulate time taken to produce an item
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
