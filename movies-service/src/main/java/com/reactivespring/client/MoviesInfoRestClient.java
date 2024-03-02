package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MoviesInfoRestClient {
    private final WebClient webClient;

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {

    }
}