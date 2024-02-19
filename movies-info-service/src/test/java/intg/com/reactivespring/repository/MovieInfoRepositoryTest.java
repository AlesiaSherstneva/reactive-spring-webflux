package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryTest {
    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        MovieInfo firstMovie = new MovieInfo(null, "Batman Begins", 2005,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        MovieInfo secondMovie = new MovieInfo(null, "The Dark Knight", 2008,
                List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18"));
        MovieInfo thirdMovie = new MovieInfo("abc", "Dark Knight Rises", 2012,
                List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        List<MovieInfo> movieInfos = List.of(firstMovie, secondMovie, thirdMovie);
        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @Test
    void findAllMovieInfosTest() {
        Flux<MovieInfo> movieInfoFlux = movieInfoRepository.findAll();

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findMovieInfoByIdTest() {
        Mono<MovieInfo> movieInfoMono = movieInfoRepository.findById("abc");

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo ->
                        assertEquals("Dark Knight Rises", movieInfo.getName()))
                .verifyComplete();
    }

    @Test
    void saveMovieInfoTest() {
        MovieInfo newMovie = new MovieInfo(null, "Batman Begins1", 2005,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        Mono<MovieInfo> savedMovieMono = movieInfoRepository.save(newMovie);

        StepVerifier.create(savedMovieMono)
                .assertNext(movieInfo ->
                        assertEquals("Batman Begins1", movieInfo.getName()))
                .verifyComplete();
    }

    @Test
    void updateMovieInfoTest() {
        MovieInfo receivedMovieInfo = movieInfoRepository.findById("abc").block();
        assert receivedMovieInfo != null;
        receivedMovieInfo.setYear(2021);

        Mono<MovieInfo> updatedMovieMono = movieInfoRepository.save(receivedMovieInfo);

        StepVerifier.create(updatedMovieMono)
                .assertNext(movieInfo ->
                        assertEquals(2021, movieInfo.getYear()))
                .verifyComplete();
    }

    @Test
    void deleteMovieInfoTest() {
        movieInfoRepository.deleteById("abc").block();
        Flux<MovieInfo> movieInfoFlux = movieInfoRepository.findAll();

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findMovieInfosByYearTest() {
        Flux<MovieInfo> movieInfoFlux = movieInfoRepository.findByYear(2005);

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findMovieInfosByNameTest() {
        Mono<MovieInfo> movieInfoMono = movieInfoRepository.findByName("The Dark Knight");

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertNotNull(movieInfo.getMovieInfoId());
                    assertEquals("The Dark Knight", movieInfo.getName());
                    assertEquals(2008, movieInfo.getYear());
                })
                .verifyComplete();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }
}