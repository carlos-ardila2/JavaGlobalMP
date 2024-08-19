package com.epam.jmp.microcollector.jobs;

import com.epam.jmp.microcollector.clients.MessagesConsumerClient;
import com.epam.jmp.microcollector.repositories.MessagesRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger infoLog = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final Logger errorLog = LoggerFactory.getLogger("errorLog");

    MessagesConsumerClient messagesConsumerClient;
    MessagesRepository messagesRepository;

    private final Counter counter;

    public ScheduledTasks(MessagesConsumerClient messagesConsumerClient, MessagesRepository messagesRepository,
                          MeterRegistry meterRegistry) {
        this.messagesConsumerClient = messagesConsumerClient;
        this.messagesRepository = messagesRepository;

        this.counter = Counter.builder("messages.received")
                .description("Total number of messages received")
                .register(meterRegistry);
    }

    @Scheduled(fixedRate = 10000)
    public void retrieveMessages() {
        try {
            var response = messagesConsumerClient.getMessagesByFeign();
            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                var messageBatch = response.getBody();

                if (messageBatch != null && !messageBatch.isEmpty()) {
                    infoLog.info("Received {} messages from sender", messageBatch.size());
                    messageBatch.forEach(message -> {
                        // This prevents duplicated ids in the database
                        message.setId(null);
                        counter.increment();
                        infoLog.info("Received message: {} from {}", message.getMessage(), message.getRecipient());
                    });
                    messagesRepository.saveAll(messageBatch);
                }
            }
        } catch (Exception e) {
            errorLog.error("Error while retrieving messages: {}", e.getMessage());
        }
    }
}
