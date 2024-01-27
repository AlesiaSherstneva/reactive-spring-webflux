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

    @Test
    void namesFluxMapTest() {
        Flux<String> namesFlux = generatorService.namesFluxMap();
        StepVerifier.create(namesFlux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFluxImmutabilityTest() {
        Flux<String> namesFlux = generatorService.namesFluxImmutability();
        StepVerifier.create(namesFlux)
                .expectNext("Alex", "Ben", "Chloe")
                .verifyComplete();
    }

    @Test
    void namesFluxMapFilterTest() {
        Flux<String> namedFlux = generatorService.namesFluxMapFilter(3);
        StepVerifier.create(namedFlux)
                .expectNext("ALEX", "CHLOE")
                .verifyComplete();
    }
}