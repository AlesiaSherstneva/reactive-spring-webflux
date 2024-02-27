package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntgTest {
    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MovieInfo firstMovie = new MovieInfo(null, "Batman Begins", 2005,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        MovieInfo secondMovie = new MovieInfo(null, "The Dark Knight", 2008,
                List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18"));
        MovieInfo thirdMovie = new MovieInfo("abc", "Dark Knight Rises", 2012,
                List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        List<MovieInfo> movieInfos = List.of(firstMovie, secondMovie, thirdMovie);
        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @Test
    void getAllMovieInfosTest() {
        webTestClient.get()
                .uri("/v1/movie-infos")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfosByYearTest() {
        URI uri = UriComponentsBuilder.fromUriString("/v1/movie-infos")
                .queryParam("year", 2005)
                .buildAndExpand().toUri();

        webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getMovieInfoByIdTest() {
        String movieInfoId = "abc";

        webTestClient.get()
                .uri("/v1/movie-infos/{id}", movieInfoId)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");

                /* .expectBody(MovieInfo.class)
                    .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo response = movieInfoEntityExchangeResult.getResponseBody();
                    assert response != null;
                    assertEquals("Dark Knight Rises", response.getName());
                }); */
    }

    @Test
    void getMovieInfoByIdNotFoundTest() {
        String movieInfoId = "def";

        webTestClient.get()
                .uri("/v1/movie-infos/{id}", movieInfoId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void addMovieInfoTest() {
        MovieInfo newMovie = new MovieInfo(null, "Batman Begins1", 2005,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.post()
                .uri("/v1/movie-infos")
                .bodyValue(newMovie)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo response = movieInfoEntityExchangeResult.getResponseBody();
                    assert response != null;
                    assertNotNull(response.getMovieInfoId());
                });
    }

    @Test
    void updateMovieInfoTest() {
        String movieInfoId = "abc";
        MovieInfo updatedMovie = new MovieInfo(null, "Dark Knight Rises1", 2005,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.put()
                .uri("/v1/movie-infos/{id}", movieInfoId)
                .bodyValue(updatedMovie)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo response = movieInfoEntityExchangeResult.getResponseBody();
                    assert response != null;
                    assertNotNull(response.getMovieInfoId());
                    assertEquals("Dark Knight Rises1", response.getName());
                });
    }


    @Test
    void updateMovieInfoNotFoundTest() {
        String movieInfoId = "def";
        MovieInfo updatedMovie = new MovieInfo(movieInfoId, "Dark Knight Rises1", 2005,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.put()
                .uri("/v1/movie-infos/{id}", movieInfoId)
                .bodyValue(updatedMovie)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteMovieInfoTest() {
        String movieInfoId = "abc";

        webTestClient.delete()
                .uri("/v1/movie-infos/{id}", movieInfoId)
                .exchange()
                .expectStatus().isNoContent();

        Flux<MovieInfo> movieInfoFlux = movieInfoRepository.findAll();
        StepVerifier.create(movieInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }
}