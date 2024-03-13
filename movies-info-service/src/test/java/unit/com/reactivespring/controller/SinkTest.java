package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinkTest {
    @Test
    void sinkTest() {
        Sinks.Many<Integer> replySink = Sinks.many().replay().all();

        replySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux1 = replySink.asFlux();
        integerFlux1.subscribe(i -> System.out.println("Subscriber 1: " + i));

        Flux<Integer> integerFlux2 = replySink.asFlux();
        integerFlux2.subscribe(i -> System.out.println("Subscriber 2: " + i));

        replySink.tryEmitNext(3);
    }

    @Test
    void sinkMulticastTest() {
        Sinks.Many<Integer> multicast = Sinks.many().multicast().onBackpressureBuffer();

        multicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        multicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux1 = multicast.asFlux();
        integerFlux1.subscribe(i -> System.out.println("Subscriber 1: " + i));

        Flux<Integer> integerFlux2 = multicast.asFlux();
        integerFlux2.subscribe(i -> System.out.println("Subscriber 2: " + i));

        multicast.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    @Test
    void sinkUnicastTest() {
        Sinks.Many<Integer> unicast = Sinks.many().unicast().onBackpressureBuffer();

        unicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        unicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux1 = unicast.asFlux();
        integerFlux1.subscribe(i -> System.out.println("Subscriber 1: " + i));

        unicast.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}