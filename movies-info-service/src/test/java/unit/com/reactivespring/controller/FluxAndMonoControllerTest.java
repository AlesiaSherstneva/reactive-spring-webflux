package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
class FluxAndMonoControllerTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    void firstFluxTest() {
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class).hasSize(3);
    }

    @Test
    void secondFluxTest() {
        Flux<Integer> response = webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(response)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    void thirdFluxTest() {
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(listEntityExchangeResult -> {
                    List<Integer> response = listEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(response).size() == 3;
                });
    }

    @Test
    void monoTest() {
        webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    String response = stringEntityExchangeResult.getResponseBody();
                    assertEquals("Hello world", response);
                });
    }

    @Test
    void streamTest() {
        Flux<Long> response = webTestClient.get()
                .uri("/stream")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(response)
                .expectNext(0L, 1L, 2L, 3L)
                .thenCancel()
                .verify();
    }
}