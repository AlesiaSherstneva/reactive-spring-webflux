package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

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
        Flux<String> namesFlux = generatorService.namesFluxMapFilter(3);
        StepVerifier.create(namesFlux)
                .expectNext("ALEX", "CHLOE")
                .verifyComplete();
    }

    @Test
    void namedFluxFlatMapTest() {
        Flux<String> namesFlux = generatorService.namesFluxFlatMap();
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "B", "E", "N", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namedFluxFlatMapAsyncTest() {
        Flux<String> namesFlux = generatorService.namesFluxFlatMapAsync();
        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    void namedFluxConcatMapTest() {
        Flux<String> namesFlux = generatorService.namesFluxConcatMap();
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "B", "E", "N", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMapTest() {
        Mono<List<String>> namesMono = generatorService.namesMonoFlatMap();
        StepVerifier.create(namesMono)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMapManyTest() {
        Flux<String> namesFlux = generatorService.namesMonoFlatMapMany();
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFluxTransformTest() {
        Flux<String> namesFlux = generatorService.namesFluxTransform(3);
        StepVerifier.create(namesFlux)
                .expectNext("ALEX", "CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFluxTransformEmptyTest() {
        Flux<String> namesFlux = generatorService.namesFluxTransform(6);
        StepVerifier.create(namesFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFluxTransformSwitchIfEmptyTest() {
        Flux<String> namesFlux = generatorService.namesFluxTransformSwitchIfEmpty(6);
        StepVerifier.create(namesFlux)
                .expectNext("DEFAULT")
                .verifyComplete();
    }

    @Test
    void exploreConcatTest() {
        Flux<String> concatFlux = generatorService.exploreConcat();
        testConcatFlux(concatFlux);
    }

    @Test
    void exploreConcatWithFluxTest() {
        Flux<String> concatFlux = generatorService.exploreConcatWithFlux();
        testConcatFlux(concatFlux);
    }

    private static void testConcatFlux(Flux<String> concatFlux) {
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreConcatWithMonoTest() {
        Flux<String> namesFlux = generatorService.exploreConcatWithMono();
        StepVerifier.create(namesFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }
}