package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MoviesInfoService moviesInfoService;

    @Test
    void getAllMoviesInfoTest() {
        MovieInfo firstMovie = new MovieInfo(null, "Batman Begins", 2005,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        MovieInfo secondMovie = new MovieInfo(null, "The Dark Knight", 2008,
                List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18"));
        MovieInfo thirdMovie = new MovieInfo("abc", "Dark Knight Rises", 2012,
                List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        List<MovieInfo> movieInfos = List.of(firstMovie, secondMovie, thirdMovie);

        when(moviesInfoService.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieInfos));

        webTestClient.get()
                .uri("/v1/movie-infos")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfoByIdTest() {
        MovieInfo movieInBase = new MovieInfo("abc", "Dark Knight Rises", 2012,
                List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        String movieInfoId = "abc";

        when(moviesInfoService.getMovieInfoById(anyString())).thenReturn(Mono.just(movieInBase));

        webTestClient.get()
                .uri("/v1/movie-infos/{id}", movieInfoId)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo response = movieInfoEntityExchangeResult.getResponseBody();
                    assert response != null;
                    assertEquals("Dark Knight Rises", response.getName());
                });
    }

    @Test
    void addMovieInfoTest() {
        MovieInfo newMovie = new MovieInfo("mockId", "Batman Begins1", 2005,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(moviesInfoService.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(newMovie));

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
                    assertEquals("mockId", response.getMovieInfoId());
                });
    }

    @Test
    void updateMovieInfoTest() {
        String movieInfoId = "abc";
        MovieInfo updatedMovie = new MovieInfo(movieInfoId, "Dark Knight Rises1", 2005,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(moviesInfoService.updateMovieInfo(isA(MovieInfo.class), isA(String.class)))
                .thenReturn(Mono.just(updatedMovie));

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
    void deleteMovieInfoTest() {
        String movieInfoId = "abc";

        webTestClient.delete()
                .uri("/v1/movie-infos/{id}", movieInfoId)
                .exchange()
                .expectStatus().isNoContent();
    }
}