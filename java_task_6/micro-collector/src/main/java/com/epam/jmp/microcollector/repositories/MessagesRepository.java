package com.epam.jmp.microcollector.repositories;

import com.epam.jmp.microcollector.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface MessagesRepository extends MongoRepository<Message, BigInteger> {
}
