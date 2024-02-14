package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {
    private final MoviesInfoService moviesInfoService;

    @Autowired
    public MoviesInfoController(MoviesInfoService moviesInfoService) {
        this.moviesInfoService = moviesInfoService;
    }

    @GetMapping("/movie-infos")
    public Flux<MovieInfo> getAllMovieInfos() {
        return moviesInfoService.getAllMovieInfos();
    }

    @GetMapping("/movie-infos/{id}")
    public Mono<MovieInfo> getMovieInfoById(@PathVariable String id) {
        return moviesInfoService.getMovieInfoById(id);
    }

    @PostMapping("/movie-infos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody MovieInfo movieInfo) {
        return moviesInfoService.addMovieInfo(movieInfo);
    }
}