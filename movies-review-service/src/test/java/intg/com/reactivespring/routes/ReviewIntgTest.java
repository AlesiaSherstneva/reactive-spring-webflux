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
        Review first = new Review(null, 1L, "Awesome movie", 9.0);
        Review second = new Review(null, 1L, "Awesome movie1", 9.0);
        Review third = new Review(null, 2L, "Excellent movie", 8.0);

        List<Review> reviewList = List.of(first, second, third);
        reviewReactiveRepository.saveAll(reviewList).blockLast();
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

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }
}