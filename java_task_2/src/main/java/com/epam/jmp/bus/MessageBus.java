package com.epam.jmp.bus;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class MessageBus {

    public static final int MAX_QUEUE_SIZE = 100;

    private final Queue<Message> queue = new LinkedList<>();

    public void publish(Message message) {
        synchronized (queue) {
            queue.add(message);
            queue.notifyAll();
        }
    }

    public Optional<Message> consume(String topic) {
        Message message = queue.peek();
        if (message != null && message.topic().equals(topic)) {
            synchronized (queue) {
                queue.poll();
            }
            return Optional.of(message);
        }
        return Optional.empty();
    }

    public void standBy() {
        synchronized (queue) {
            try {
                queue.wait();
            } catch (InterruptedException e) {
                System.err.println("INTERRUPTED");
            }
        }
    }
}
