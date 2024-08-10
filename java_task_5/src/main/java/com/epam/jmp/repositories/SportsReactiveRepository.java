package com.epam.jmp.repositories;

import com.epam.jmp.model.Sport;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SportsReactiveRepository extends ReactiveMongoRepository<Sport, String> {

    Flux<Sport> findAllBy(TextCriteria criteria);
}
