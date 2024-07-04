package com.epam.jmp.bus;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.epam.jmp.bus.MessageBus.MAX_QUEUE_SIZE;

public class Consumer implements Runnable {

    private final MessageBus messageBus;
    private final String topic;

    public Consumer(MessageBus messageBus, String topic) {
        this.messageBus = messageBus;
        this.topic = topic;
    }

    @Override
    public void run() {
        AtomicBoolean done = new AtomicBoolean(false);
        while (!done.get()){
            messageBus.consume(topic).ifPresentOrElse(
                message -> {
                    System.out.println("Consumed: " + message);
                    if (message.content() == MAX_QUEUE_SIZE) {
                        System.out.printf("Consumer %s done!\n", topic);
                        done.set(true);
                    }
                }, messageBus::standBy
            );
        }
    }
}
