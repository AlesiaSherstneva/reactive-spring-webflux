package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {
    public static void main(String[] args) {
        FluxAndMonoGeneratorService generatorService = new FluxAndMonoGeneratorService();
        generatorService.namesFlux()
                .subscribe(name -> System.out.printf("Name is: %s\n", name));
        generatorService.namesMono()
                .subscribe(name -> System.out.printf("Mono name is: %s\n", name));
    }

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("Alex", "Ben", "Chloe"));
    }

    public Mono<String> namesMono() {
        return Mono.just("Alex");
    }

    public Flux<String> namesFluxMap() {
        return namesFlux().map(String::toUpperCase);
    }

    public Flux<String> namesFluxImmutability() {
        Flux<String> namesFlux = Flux.fromIterable(List.of("Alex", "Ben", "Chloe"));
        namesFlux.map(String::toUpperCase);
        return namesFlux; // words are not in upper case
    }

    public Flux<String> namesFluxMapFilter(int stringLength) {
        return namesFlux()
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);
    }

    public Flux<String> namesFluxFlatMap() {
        return Flux.just("ALEX", "BEN", "CHLOE")
                .flatMap(this::splitString);
    }

    private Flux<String> splitString(String name) {
        return Flux.just(name.split(""));
    }

    public Flux<String> namesFluxFlatMapAsync() {
        return Flux.just("ALEX", "BEN", "CHLOE")
                .flatMap(this::splitStringWithDelay); // letters return in async order
    }

    public Flux<String> namesFluxConcatMap() {
        return Flux.just("ALEX", "BEN", "CHLOE")
                .concatMap(this::splitStringWithDelay); // async, but return right order
    }

    public Flux<String> namesFluxTransform(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap =
                name -> name.map(String::toUpperCase)
                        .filter(s -> s.length() > stringLength);

        return Flux.just("Alex", "Ben", "Chloe")
                .transform(filterMap);
    }

    private Flux<String> splitStringWithDelay(String name) {
        int delay = new Random().nextInt(1000);
        return Flux.just(name.split(""))
                .delayElements(Duration.ofMillis(delay));
    }

    public Mono<List<String>> namesMonoFlatMap() {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .flatMap(this::splitStringMono);
    }

    private Mono<List<String>> splitStringMono(String name) {
        String[] charArray = name.split("");
        List<String> charList = List.of(charArray);
        return Mono.just(charList);
    }

    public Flux<String> namesMonoFlatMapMany() {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .flatMapMany(this::splitString);
    }
}