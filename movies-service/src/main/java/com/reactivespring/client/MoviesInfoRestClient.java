package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.util.RetryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class MoviesInfoRestClient {
    private final WebClient webClient;

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        String url = moviesInfoUrl.concat("/{id}");

        return webClient.get()
                .uri(url, movieId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> {
                            log.info("Status code is: {}", clientResponse.statusCode().value());
                            if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                                return Mono.error(new MoviesInfoClientException(
                                        "There is no movie info with id " + movieId,
                                        clientResponse.statusCode().value()));
                            }
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(
                                            responseMessage,
                                            clientResponse.statusCode().value()
                                    )));
                        })
                .onStatus(HttpStatus::is5xxServerError,
                        clientResponse -> {
                            log.info("Status code is: {}", clientResponse.statusCode().value());
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(responseMessage -> Mono.error(new MoviesInfoServerException(
                                            String.format("Server Exception in movies info service: %s", responseMessage)
                                    )));
                        })
                .bodyToMono(MovieInfo.class)
                //.retry(3); // retries immediately 3 times
                .retryWhen(RetryUtil.retrySpec());
    }

    public Flux<MovieInfo> retrieveMovieInfoStream() {
        String url = moviesInfoUrl.concat("/stream");

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> {
                            log.info("Status code is: {}", clientResponse.statusCode().value());
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(
                                            responseMessage,
                                            clientResponse.statusCode().value()
                                    )));
                        })
                .onStatus(HttpStatus::is5xxServerError,
                        clientResponse -> {
                            log.info("Status code is: {}", clientResponse.statusCode().value());
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(responseMessage -> Mono.error(new MoviesInfoServerException(
                                            String.format("Server Exception in movies info service: %s", responseMessage)
                                    )));
                        })
                .bodyToFlux(MovieInfo.class)
                //.retry(3); // retries immediately 3 times
                .retryWhen(RetryUtil.retrySpec());
    }
}