package com.epam.jmp.microrecipient.listener;

import com.epam.jmp.dto.Message;
import com.epam.jmp.microrecipient.model.MessageRecord;
import com.epam.jmp.microrecipient.repositories.MessagesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(id = "messagesReceiver", queues = "messages")
public class MessagesReceiver {

    Logger logger = LoggerFactory.getLogger(MessagesReceiver.class);

    private final MessagesRepository messagesRepository;

    public MessagesReceiver(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    @RabbitHandler
    public void receive(Message message) {
        logger.info("Received message: {} from {}", message.getMessage(), message.getRecipient());
        messagesRepository.save(new MessageRecord(message));
    }
}
