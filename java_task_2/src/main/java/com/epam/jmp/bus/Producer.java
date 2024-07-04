package com.epam.jmp.bus;

import java.time.LocalDateTime;

import static com.epam.jmp.bus.MessageBus.MAX_QUEUE_SIZE;

public class Producer implements Runnable {

    private final MessageBus messageBus;
    private final String topic;
    private final String senderId;

    public Producer(MessageBus messageBus, String topic, String senderId) {
        this.messageBus = messageBus;
        this.topic = topic;
        this.senderId = senderId;
    }

    @Override
    public void run() {
        for (int i = 0; i <= MAX_QUEUE_SIZE; i++) {
            Message message = new Message(i, topic, senderId, LocalDateTime.now());
            messageBus.publish(message);
            System.out.println("Published: " + message);
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                System.err.println("INTERRUPTED");
            }
        }
        System.out.printf("Producer %s done!\n", topic);
    }
}
