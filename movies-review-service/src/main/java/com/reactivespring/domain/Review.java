package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Review {
    @Id
    private String reviewId;

    @NotNull(message = "Movie info id must not be null")
    private Long movieInfoId;

    private String comment;

    @Min(value = 0L, message = "Rating must be a non-negative value")
    private Double rating;
}