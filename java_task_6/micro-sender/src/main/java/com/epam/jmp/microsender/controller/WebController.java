package com.epam.jmp.microsender.controller;

import com.epam.jmp.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class WebController {

    private static final Logger infoLog = LoggerFactory.getLogger(WebController.class);
    private static final Logger errorLog = LoggerFactory.getLogger("errorLog");

    private final RabbitTemplate template;
    private final Queue queue;

    public WebController(RabbitTemplate template, Queue queue) {
        this.template = template;
        this.queue = queue;
    }

    @PostMapping("/notification")
    public ResponseEntity<Void> doNotification(@RequestBody Message message) {

        try {
            template.convertAndSend(queue.getName(), message);
        } catch (AmqpException e) {
            errorLog.error("Error while sending message", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        infoLog.info("Received message: {} from {}", message.getMessage(), message.getRecipient());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}