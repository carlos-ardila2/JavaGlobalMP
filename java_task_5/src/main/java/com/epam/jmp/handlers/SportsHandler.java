package com.epam.jmp.handlers;

import com.epam.jmp.model.Sport;
import com.epam.jmp.repositories.SportsReactiveRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;

@Service
public class SportsHandler {

    private SportsReactiveRepository sportsRepository;
    private WebClient webClient;
    private String language;
    private int batchSize;

    public SportsHandler(SportsReactiveRepository sportsRepository,
                         @Value("${sports.language}") String language,
                         @Value("${sports.records.batchSize}") int batchSize,
                         @Value("${sports.external.url}") String externalUrl) {
        this.sportsRepository = sportsRepository;
        this.batchSize = batchSize;
        this.language = language;

        if (externalUrl != null && !externalUrl.isBlank()) {
            this.webClient = WebClient.create(externalUrl);
        }
    }

    public Flux<Sport> getSports(String refresh) {
        if (!refresh.isBlank()) {
            ArrayList<Sport> sportsToSave = new ArrayList<>();
            return webClient.get().uri("/")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .exchangeToFlux(res -> res.statusCode().is2xxSuccessful() ? res.bodyToFlux(Sport.class) :
                                Flux.error(new RuntimeException("Error while fetching sports from external service.")))
                    .log()
                    .limitRate(batchSize)
                    .onBackpressureBuffer()
                    .doOnNext(sportsToSave::add)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnComplete(() -> sportsRepository.saveAll(sportsToSave).subscribe());
        } else {
            return sportsRepository.findAll().limitRate(batchSize).log();
        }
    }

    public Mono<Sport> getSportById(String id) {
        return sportsRepository.findById(id);
    }

    public Mono<Void> deleteSport(String id) {
        return sportsRepository.deleteById(id);
    }

    public Mono<Sport> saveSport(Sport sport) {
        if (sport.getId() == null) {
            throw new IllegalArgumentException("Sport id is required");
        }

        return sportsRepository.existsById(sport.getId())
            .flatMap(sportExists -> sportExists ? Mono.empty() : sportsRepository.save(sport));
    }

    public Mono<Sport> updateSport(String id, Sport sport) {
        return sportsRepository.findById(id)
                .flatMap(savedSport -> sportsRepository.save(savedSport.merge(sport).setNew(false)));
    }

    public Flux<Sport> searchSports(String query) {
        if (!query.isEmpty()) {
            var searchItems = query.split(",");
            return sportsRepository.findAllBy(TextCriteria.forLanguage(language)
                    .caseSensitive(false).diacriticSensitive(false).matchingAny(searchItems));
        } else {
            return sportsRepository.findAll();
        }
    }
}
