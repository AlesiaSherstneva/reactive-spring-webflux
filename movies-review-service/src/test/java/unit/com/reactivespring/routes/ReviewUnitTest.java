package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class})
@AutoConfigureWebTestClient
public class ReviewUnitTest {
    static String REVIEWS_URL = "/v1/reviews";

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    private List<Review> reviews;

    @Test
    void getAllReviewsTest() {
        reviews = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            reviews.add(new Review());
        }

        when(reviewReactiveRepository.findAll()).thenReturn(Flux.fromIterable(reviews));

        webTestClient.get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .hasSize(5);
    }

    @Test
    void getReviewByMovieIdTest() {
        URI uri = UriComponentsBuilder.fromUriString(REVIEWS_URL)
                .queryParam("movieInfoId", 1L)
                .buildAndExpand().toUri();

        reviews = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            reviews.add(new Review());
        }

        when(reviewReactiveRepository.findReviewsByMovieInfoId(anyLong())).thenReturn(Flux.fromIterable(reviews));

        webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void addReviewTest() {
        Review newReview = new Review("abc", 1L, "Awesome movie", 9.0);

        when(reviewReactiveRepository.save(isA(Review.class))).thenReturn(Mono.just(newReview));

        webTestClient.post()
                .uri(REVIEWS_URL)
                .bodyValue(newReview)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    Review savedReview = reviewEntityExchangeResult.getResponseBody();
                    assert savedReview != null;
                    assert savedReview.getReviewId() != null;
                    assertEquals("Awesome movie", savedReview.getComment());
                });
    }

    @Test
    void updateReviewTest() {
        Review oldReview = new Review("abc", 1L, "Awesome movie", 9.0);
        Review updatedReview = new Review("abc", 1L, "Awful stupid movie", 9.0);
        String reviewId = oldReview.getReviewId();

        when(reviewReactiveRepository.findById(anyString())).thenReturn(Mono.just(oldReview));
        when(reviewReactiveRepository.save(isA(Review.class))).thenReturn(Mono.just(updatedReview));

        webTestClient.put()
                .uri(REVIEWS_URL + "/{id}", reviewId)
                .bodyValue(updatedReview)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    Review response = reviewEntityExchangeResult.getResponseBody();
                    assert response != null;
                    assertEquals(reviewId, response.getReviewId());
                    assertEquals("Awful stupid movie", response.getComment());
                });
    }

    @Test
    void deleteReviewTest() {
        Review deletedReview = new Review("mockId", 1L, "Awful stupid movie", 9.0);
        String reviewId = deletedReview.getReviewId();

        when(reviewReactiveRepository.findById(anyString())).thenReturn(Mono.just(deletedReview));
        when(reviewReactiveRepository.deleteById(anyString())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(REVIEWS_URL + "/{id}", reviewId)
                .exchange()
                .expectStatus().isNoContent();
    }
}