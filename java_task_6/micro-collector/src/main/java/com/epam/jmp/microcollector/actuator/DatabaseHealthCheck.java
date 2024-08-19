package com.epam.jmp.microcollector.actuator;

import org.springframework.boot.actuate.data.mongo.MongoHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component("messageCollectorDatabase")
public class DatabaseHealthCheck implements HealthIndicator {

    final MongoTemplate template;
    final MongoHealthIndicator mongoIndicator;

    public DatabaseHealthCheck(MongoTemplate mongoTemplate) {
        this.template = mongoTemplate;
        this.mongoIndicator = new MongoHealthIndicator(template);
    }

    @Override
    public Health health() {
        return mongoIndicator.health();
    }
}
