package com.epam.jmp.threads;

import java.util.Map;

public class WriteThread extends Thread {

    Map<Integer,Integer> map;

    public WriteThread(Map<Integer,Integer> map) {
        this.map = map;
    }

    @Override
    public void run() {

        for (int i = 0; i < 3_800; i++) {
            map.put(i,i*10);
            System.out.println("Pushed: " + i);
        }
        System.out.println("Thread 1 done!");
    }
}
