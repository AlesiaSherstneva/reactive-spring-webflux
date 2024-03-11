package com.reactivespring.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(properties = {
        "restClient.moviesInfoUrl=http://localhost:8084/v1/movie-infos",
        "restClient.reviewsUrl=http://localhost:8084/v1/reviews"
})
public class MoviesControllerIntgTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    void retrieveMovieByIdTest() {
        String movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movie-infos/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movie-info.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("reviews.json")));

        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    Movie response = movieEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(response).getReviewList().size() == 2;
                    assertEquals("Batman Begins", response.getMovieInfo().getName());
                });
    }

    @Test
    void retrieveMovieByIdNotFoundTest() {
        String movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movie-infos/" + movieId))
                .willReturn(aResponse().withStatus(404)));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("reviews.json")));

        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("There is no movie info with id abc");

        WireMock.verify(1, getRequestedFor(urlEqualTo("/v1/movie-infos/" + movieId)));
    }

    @Test
    void retrieveMovieByIdReviewsNotFoundTest() {
        String movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movie-infos/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movie-info.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse().withStatus(404)));

        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    Movie response = movieEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(response).getReviewList().size() == 0;
                    assertEquals("Batman Begins", response.getMovieInfo().getName());
                });
    }

    @Test
    void retrieveMovieByIdServerErrorTest() {
        String movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movie-infos/" + movieId))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Movie info service unavailable")));

        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server Exception in movies info service: Movie info service unavailable");

        WireMock.verify(4, getRequestedFor(urlEqualTo("/v1/movie-infos/" + movieId)));
    }

    @Test
    void retrieveMovieByIdReviewsServerErrorTest() {
        String movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movie-infos/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movie-info.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Reviews service unavailable")));

        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server Exception in movies reviews service: Reviews service unavailable");

        WireMock.verify(4, getRequestedFor(urlPathMatching("/v1/reviews*")));
    }
}