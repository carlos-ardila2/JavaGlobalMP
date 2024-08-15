package com.epam.jmp.microrecipient.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableRabbit
public class AppConfig {
    /*@Bean
    public DirectMessageListenerContainer customRabbitListenerContainerFactory() {
        DirectMessageListenerContainer factory = new DirectMessageListenerContainer();
        factory.setConnectionFactory(rabbitConnectionFactory());
        factory.setConsumersPerQueue(3);
        factory.setMonitorInterval(3000);
        return factory;
    }

    @Bean
    public CachingConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost", 5672);
        connectionFactory.setUsername("jmp-user");
        connectionFactory.setPassword("secret");
        return connectionFactory;
    }*/

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("com.epam.jmp.dto.*"));
        return converter;
    }
}