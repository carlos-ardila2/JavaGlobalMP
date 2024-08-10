package com.epam.jmp.config;


import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories(value = "com.epam.jmp.repositories")
public class MongoReactiveApplication extends AbstractReactiveMongoConfiguration {

    @Bean
    public MongoClient mongoClient() {
        return super.reactiveMongoClient();
    }

    @Override
    public String getDatabaseName() {
        return "sports";
    }
}