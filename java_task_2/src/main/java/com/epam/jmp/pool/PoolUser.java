package com.epam.jmp.pool;

import java.util.Random;

public class PoolUser implements Runnable {

    private static final Random random = new Random();
    private static final int TOTAL_OPERATIONS = 10;

    private final int id = Math.abs(random.nextInt());

    private final boolean writeObjects = id  % 2 == 0;

    private final BlockingObjectPool pool;

    public PoolUser(BlockingObjectPool pool) {
        this.pool = pool;
    }

    @Override
    public void run() {
        System.out.printf("Starting PoolUser %s thread %d ...\n", (writeObjects ? "write" : "read"),
                Thread.currentThread().threadId());
        for (int i = 0; i < TOTAL_OPERATIONS; i++) {
            if (writeObjects) {
                Object object = "Object " + Thread.currentThread().threadId() + " - " + i;
                pool.take(object);
                System.out.println(Thread.currentThread().threadId() + " pushed new " + object);
            } else {
                Object object = pool.get();
                if (object == null) {
                    i--;
                } else {
                    System.out.println(Thread.currentThread().threadId() + " got: " + object);
                }
            }
        }
        System.out.printf("PoolUser %s thread %d done!\n", (writeObjects ? "write" : "read"),
                Thread.currentThread().threadId());
    }
}
