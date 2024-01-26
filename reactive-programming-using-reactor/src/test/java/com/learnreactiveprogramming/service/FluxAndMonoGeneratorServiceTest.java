package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService generatorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFluxTest() {
        Flux<String> namesFlux = generatorService.namesFlux();
        StepVerifier.create(namesFlux)
                // .expectNextCount(3)
                .expectNext("Alex", "Ben", "Chloe")
                // .expectNext("Alex")
                // .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesMonoTest() {
        Mono<String> namesMono = generatorService.namesMono();
        StepVerifier.create(namesMono)
                .expectNext("Alex")
                .verifyComplete();
    }
}