package com.epam.jmp.routers;

import com.epam.jmp.handlers.SportsHandler;
import com.epam.jmp.model.Sport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;
import static reactor.core.publisher.Mono.just;

@Configuration
public class WebRouter {

    private final SportsHandler handler;

    public static final String API_V1_SPORTS = "/api/v1/sports";

    public WebRouter(SportsHandler handler) {
        this.handler = handler;
    }

    @Bean
    public RouterFunction<ServerResponse> composedRoutes() {
        return
            route(GET("/api/v1/sports"),
                    req -> ok().contentType(MediaType.APPLICATION_NDJSON).body(
                            handler.getSports(req.queryParam("refresh").orElse("")),
                                Sport.class))

            .and(route(GET("/api/v1/sports/search"),
                    req -> ok().body(
                            handler.searchSports(req.queryParam("q").orElse("")),
                            Sport.class)))

            .and(route(GET("/api/v1/sports/{id}"),
                    req -> handler.getSportById(req.pathVariable("id"))
                                    .flatMap(sport -> ok().body(just(sport), Sport.class))
                                    .switchIfEmpty(status(HttpStatus.NOT_FOUND).bodyValue("Sport not found."))))

            .and(route(DELETE("/api/v1/sports/{id}"),
                    req -> handler.deleteSport(req.pathVariable("id"))
                            .then(ok().build())))

            .and(route(PATCH("/api/v1/sports/{id}"),
                    req -> req.bodyToMono(Sport.class).flatMap(sport ->
                                    handler.updateSport(req.pathVariable("id"), sport))
                            .flatMap(updated -> ok().body(just(updated), Sport.class))
                            .switchIfEmpty(status(HttpStatus.NOT_FOUND).bodyValue("Sport not found."))))

            .and(route(PUT("/api/v1/sports"),
                    req -> req.bodyToMono(Sport.class).flatMap(handler::saveSport)
                            .flatMap(saved -> created(URI.create(API_V1_SPORTS + "/" + saved.getId())).build())
                            .switchIfEmpty(status(HttpStatus.CONFLICT).bodyValue("A sport with the provided id already exists."))
                            .onErrorResume(e -> status(HttpStatus.BAD_REQUEST).bodyValue("Bad input: " + e.getMessage())))
            );
    }
}
