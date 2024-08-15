package com.epam.jmp.microcollector.repositories;

import com.epam.jmp.microcollector.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessagesRepository extends MongoRepository<Message, Long> {
}
