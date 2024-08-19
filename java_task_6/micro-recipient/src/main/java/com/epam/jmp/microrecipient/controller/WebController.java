package com.epam.jmp.microrecipient.controller;

import com.epam.jmp.microrecipient.repositories.MessagesRepository;
import com.epam.jmp.microrecipient.model.MessageRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        if (!pendingMessages.isEmpty()) {
            logger.info("Found {} messages in temporary storage", pendingMessages.size());
            messagesRepository.deleteAll(pendingMessages);
        }

        return pendingMessages;
    }

    @RequestMapping(value = "/messages", method = RequestMethod.HEAD)
    public ResponseEntity<Void> getMessagesStatus() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
