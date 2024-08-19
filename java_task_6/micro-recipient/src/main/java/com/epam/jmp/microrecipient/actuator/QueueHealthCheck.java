package com.epam.jmp.microrecipient.actuator;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.amqp.RabbitHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("messageRecipientQueue")
public class QueueHealthCheck implements HealthIndicator {

    final RabbitTemplate template;
    final RabbitHealthIndicator indicator;

    public QueueHealthCheck(RabbitTemplate template) {
        this.template = template;
        this.indicator = new RabbitHealthIndicator(template);
    }

    @Override
    public Health health() {
        return indicator.health();
    }
}
