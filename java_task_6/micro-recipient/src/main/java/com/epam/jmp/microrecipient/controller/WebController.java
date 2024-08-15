package com.epam.jmp.microrecipient.controller;

import com.epam.jmp.microrecipient.repositories.MessagesRepository;
import com.epam.jmp.microrecipient.model.MessageRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class WebController {

    Logger logger = LoggerFactory.getLogger(WebController.class);

    private final MessagesRepository messagesRepository;

    public WebController(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    @GetMapping("/messages")
    public List<MessageRecord> getMessages() {
        var pendingMessages = messagesRepository.findAll();
        messagesRepository.deleteAll(pendingMessages);

        logger.info("Extracted {} messages from temporary storage", pendingMessages.size());
        return pendingMessages;
    }

}
