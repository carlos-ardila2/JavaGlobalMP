package com.epam.jmp.microcollector.jobs;

import com.epam.jmp.microcollector.clients.MessagesConsumerClient;
import com.epam.jmp.microcollector.repositories.MessagesRepository;
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

    public ScheduledTasks(MessagesConsumerClient messagesConsumerClient, MessagesRepository messagesRepository) {
        this.messagesConsumerClient = messagesConsumerClient;
        this.messagesRepository = messagesRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void retrieveMessages() {
        var response = messagesConsumerClient.getMessagesByFeign();
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            var messageBatch = response.getBody();

            if (messageBatch != null && !messageBatch.isEmpty()) {
                infoLog.info("Received {} messages from sender", messageBatch.size());
                messageBatch.forEach(message ->
                        infoLog.info("Received message: {}: {} from {}", message.getId(), message.getMessage(), message.getRecipient()));
                messagesRepository.saveAll(messageBatch);
            }
        } else {
            errorLog.error("Failed to receive messages from sender");
        }
    }
}
