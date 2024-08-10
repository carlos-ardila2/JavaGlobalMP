package com.epam.jmp;

import com.epam.jmp.handlers.SportsHandler;
import com.epam.jmp.model.Sport;
import com.epam.jmp.repositories.SportsReactiveRepository;
import com.epam.jmp.routers.WebRouter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class WebTest extends AbstractTestNGSpringContextTests {

    @Mock
    private SportsReactiveRepository sportsRepository;

    @InjectMocks
    private SportsHandler sportsHandler = new SportsHandler(sportsRepository, "english", 2, "");

    private AutoCloseable closeable;

    private WebTestClient client;

    @BeforeTest
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        WebRouter router = new WebRouter(sportsHandler);
        client =  WebTestClient.bindToRouterFunction(router.composedRoutes()).build();
    }

    @AfterTest
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test(description = "Test get single sport with valid id")
    void givenSportId_whenGetSportById_thenCorrectSport() {

        Sport football = new Sport("1", "Football");

        given(sportsRepository.findById(anyString())).willReturn(Mono.just(football));

        client.get()
                .uri(WebRouter.API_V1_SPORTS + "/1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Sport.class)
                .isEqualTo(football);
    }

    @Test(description = "Test get single sport with invalid id")
    void givenInvalidSportId_whenGetSportById_thenNoSportFound() {

        given(sportsRepository.findById(anyString())).willReturn(Mono.empty());

        client.get()
                .uri(WebRouter.API_V1_SPORTS + "/7")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(String.class)
                .isEqualTo("Sport not found.");
    }

    @Test(description = "Test delete single sport with valid id")
    void givenSportId_whenDeleteSport_thenNoError() {

        given(sportsRepository.deleteById(anyString())).willReturn(Mono.empty());

        client.delete()
                .uri(WebRouter.API_V1_SPORTS + "/1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody().isEmpty();
    }

    @Test(description = "Test save sport")
    void givenSport_whenSaveSport_thenSavedSport() {

        Sport football = new Sport("1", "Football");

        given(sportsRepository.existsById(anyString())).willReturn(Mono.just(false));
        given(sportsRepository.save(any())).willReturn(Mono.just(football));

        client.put()
                .uri(WebRouter.API_V1_SPORTS)
                .body(Mono.just(football), Sport.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test(description = "Test save sport without id")
    void givenSportWithNoId_whenSaveSport_thenBadRequest() {

        Sport football = new Sport(null, "Skateboarding");

        client.put()
                .uri(WebRouter.API_V1_SPORTS)
                .body(Mono.just(football), Sport.class)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Bad input: Sport id is required");
    }

    @Test(description = "Test save sport with repeated id")
    void givenSportWithRepeatedId_whenSaveSport_thenBadRequest() {

        Sport football = new Sport("1", "Football");

        given(sportsRepository.existsById(anyString())).willReturn(Mono.just(true));

        client.put()
                .uri(WebRouter.API_V1_SPORTS)
                .body(Mono.just(football), Sport.class)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(String.class)
                .isEqualTo("A sport with the provided id already exists.");
    }

    @Test(description = "Test update sport with invalid id")
    void givenInvalidSportId_whenUpdateSport_thenNoSportFound() {

        Sport football = new Sport("1", "Football");
        given(sportsRepository.findById(anyString())).willReturn(Mono.just(football));

        Sport realFootball = new Sport("1", "Football Soccer").setNew(false);
        given(sportsRepository.save(any())).willReturn(Mono.just(football.merge(realFootball).setNew(false)));

        client.patch()
                .uri(WebRouter.API_V1_SPORTS + "/1")
                .body(Mono.just(realFootball), Sport.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Sport.class)
                .isEqualTo(realFootball);
    }

    @Test(description = "Test get a collection of sports")
    void givenNoRefresh_whenGetSports_thenSportsFromRepository() {

        Sport football = new Sport("1", "Football");
        Sport basketball = new Sport("2", "Basketball");

        given(sportsRepository.findAll()).willReturn(Mono.just(football).concatWith(Mono.just(basketball)));

        client.get()
                .uri(WebRouter.API_V1_SPORTS)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Sport.class)
                .hasSize(2)
                .contains(football, basketball);
    }

}
