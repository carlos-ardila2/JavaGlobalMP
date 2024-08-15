package com.epam.jmp.microsender.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SenderConfig {

    @Bean
    public Queue messagesQueue() {
        return new Queue("messages");
    }
}
