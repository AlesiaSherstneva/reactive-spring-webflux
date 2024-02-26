package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewIntgTest {
    static String REVIEWS_URL = "/v1/reviews";

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        Review firstReview = new Review(null, 1L, "Awesome movie", 9.0);
        Review secondReview = new Review(null, 1L, "Awesome movie1", 9.0);
        Review thirdReview = new Review(null, 2L, "Excellent movie", 8.0);

        List<Review> reviews = List.of(firstReview, secondReview, thirdReview);
        reviewReactiveRepository.saveAll(reviews).blockLast();
    }

    @Test
    void getAllReviewsTest() {
        webTestClient.get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void getReviewByMovieIdTest() {
        URI uri = UriComponentsBuilder.fromUriString(REVIEWS_URL)
                .queryParam("movieInfoId", 1L)
                .buildAndExpand().toUri();

        webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .hasSize(2);
    }

    @Test
    void addReviewTest() {
        Review newReview = new Review(null, 1L, "Awesome movie", 9.0);

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
        Review updatedReview = reviewReactiveRepository.findReviewsByMovieInfoId(2L).blockFirst();
        assert updatedReview != null;
        updatedReview.setComment("Awful stupid movie");

        String reviewId = updatedReview.getReviewId();
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
                    assertEquals(2L, response.getMovieInfoId());
                    assertEquals(8.0, response.getRating());
                    assertEquals("Awful stupid movie", response.getComment());
                });
    }

    @Test
    void deleteReviewTest() {
        Review deletedReview = reviewReactiveRepository.findReviewsByMovieInfoId(2L).blockFirst();
        assert deletedReview != null;

        String reviewId = deletedReview.getReviewId();
        webTestClient.delete()
                .uri(REVIEWS_URL + "/{id}", reviewId)
                .exchange()
                .expectStatus().isNoContent();

        Flux<Review> reviewsFlux = reviewReactiveRepository.findAll();
        StepVerifier.create(reviewsFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }
}