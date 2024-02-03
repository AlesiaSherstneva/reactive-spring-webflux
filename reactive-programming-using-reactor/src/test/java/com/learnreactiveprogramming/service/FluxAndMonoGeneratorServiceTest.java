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
        Flux<String> concatFlux = generatorService.exploreConcatWithMono();
        StepVerifier.create(concatFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void exploreMergeTest() {
        Flux<String> mergeFlux = generatorService.exploreMerge();
        StepVerifier.create(mergeFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void exploreMergeWithFluxTest() {
        Flux<String> mergeFlux = generatorService.exploreMergeWithFlux();
        StepVerifier.create(mergeFlux)
                .expectNext("D", "A", "E", "B", "F", "C")
                .verifyComplete();
    }

    @Test
    void exploreMergeWithMonoTest() {
        Flux<String> mergeFlux = generatorService.exploreMergeWithMono();
        StepVerifier.create(mergeFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void exploreMergeSequentialTest() {
        Flux<String> mergeFlux = generatorService.exploreMergeSequential();
        StepVerifier.create(mergeFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreZipTest() {
        Flux<String> zipFlux = generatorService.exploreZip();
        testZipFlux(zipFlux);
    }

    @Test
    void exploreZipTupleTest() {
        Flux<String> zipFlux = generatorService.exploreZipTuple();
        StepVerifier.create(zipFlux)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();
    }

    @Test
    void exploreZipWithFluxTest() {
        Flux<String> zipFlux = generatorService.exploreZipWithFlux();
        testZipFlux(zipFlux);
    }

    private static void testZipFlux(Flux<String> zipFlux) {
        StepVerifier.create(zipFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void exploreZipWithMonoTest() {
        Mono<String> zipMono = generatorService.exploreZipWithMono();
        StepVerifier.create(zipMono)
                .expectNext("AB")
                .verifyComplete();
    }
}