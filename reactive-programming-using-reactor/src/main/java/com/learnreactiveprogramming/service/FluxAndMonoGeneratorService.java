package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
}