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

import java.time.LocalDate;
import java.util.List;

import static com.mongodb.assertions.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerTest {
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

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }
}