package com.epam.jmp.microcollector.actuator;

import com.epam.jmp.microcollector.clients.MessagesConsumerClient;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("messageCollectorService")
public class ServiceHealthCheck implements HealthIndicator {

    final MessagesConsumerClient client;

    public ServiceHealthCheck(MessagesConsumerClient messagesConsumerClient) {
        this.client = messagesConsumerClient;
    }

    @Override
    public Health health() {
        try {
            var response = client.getMessagesStatus();
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new Exception("Messages service is not available");
            }
        } catch (Exception e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
        return Health.down().build();
    }
}
