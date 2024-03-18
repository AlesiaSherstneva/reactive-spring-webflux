package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewHandler {
    private final ReviewReactiveRepository reviewReactiveRepository;
    private final Validator validator;

    Sinks.Many<Review> reviewsSink = Sinks.many().replay().latest();

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        Optional<String> movieInfoId = request.queryParam("movieInfoId");
        Flux<Review> reviewsFlux;

        if (movieInfoId.isPresent()) {
            reviewsFlux = reviewReactiveRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get()));
        } else {
            reviewsFlux = reviewReactiveRepository.findAll();
        }

        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewReactiveRepository::save)
                .doOnNext(review -> reviewsSink.tryEmitNext(review))
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validate(Review review) {
        Set<ConstraintViolation<Review>> reviewViolations = validator.validate(review);

        if (reviewViolations.size() > 0) {
            String errorMessage = reviewViolations.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new ReviewDataException(errorMessage);
        }
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        String reviewId = request.pathVariable("id");
        Mono<Review> existingReview = reviewReactiveRepository.findById(reviewId)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the id: " + reviewId)));

        return existingReview.flatMap(review -> request.bodyToMono(Review.class)
                .map(reqReview -> {
                    review.setComment(reqReview.getComment());
                    review.setRating(reqReview.getRating());
                    return review;
                })
                .flatMap(reviewReactiveRepository::save)
                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview)));
                // alternative - if reviewId is not valid string
                // need to comment higher switchIfEmpty()
                // .switchIfEmpty(ServerResponse.notFound().build());
                // no way to provide a message
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        String reviewId = request.pathVariable("id");
        Mono<Review> existingReview = reviewReactiveRepository.findById(reviewId);
        return existingReview.flatMap(review -> reviewReactiveRepository.deleteById(reviewId))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> getReviewsStream() {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewsSink.asFlux(), Review.class);
    }
}