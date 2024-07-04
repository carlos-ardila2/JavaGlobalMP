package com.epam.jmp.threads;

import java.util.Map;

public class ReadThread extends Thread {

    Map<Integer,Integer> map;

    public ReadThread(Map<Integer,Integer> map) {
        this.map = map;
    }

    @Override
    public void run() {
        while (map.size() < 3_800) {
            int sum = 0;
            for (int i = 0; i < map.size(); i++) {
                sum += map.getOrDefault(i, 0);
            }
            System.out.println("Sum: " + sum);
        }
        System.out.println("Thread 2 done!");
    }
}
