package com.epam.jmp.pool;

public class BlockingObjectPoolImpl implements BlockingObjectPool {

    private final int poolSize;

    private final Object[] pool;
    private int head = 0;
    private int tail = 0;
    private int count = 0;

    public BlockingObjectPoolImpl(int size) {
        this.pool = new Object[size];
        this.poolSize = size;
    }

    public synchronized Object get() {
        while (count == 0) {
            try {
                System.err.printf("Thread %s blocked on pool read...\n", Thread.currentThread().threadId());
                wait();
                System.err.printf("Thread %s unblocked!\n", Thread.currentThread().threadId());
            } catch (InterruptedException e) {
                System.err.println("INTERRUPTED");
            }
        }

        Object object = pool[head];
        pool[head] = null;
        head = (head + 1) % poolSize;
        count--;
        notifyAll();
        System.err.printf("Thread %s got %s from the pool\n", Thread.currentThread().threadId(), object);
        return object;
    }

    public synchronized void take(Object object) {
        while (count == poolSize) {
            try {
                System.err.printf("Thread %s blocked on pool write...\n", Thread.currentThread().threadId());
                wait();
                System.err.printf("Thread %s unblocked!\n", Thread.currentThread().threadId());
            } catch (InterruptedException e) {
                System.err.println("INTERRUPTED");
            }
        }
        pool[tail] = object;
        tail = (tail + 1) % poolSize;
        count++;
        notifyAll();
        System.err.printf("Thread %s pushed item to pool\n", Thread.currentThread().threadId());
    }

    public int size() {
        return count;
    }
}
