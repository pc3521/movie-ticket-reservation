package com.movieticket.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ScreeningDto {

    private Long id;

    @NotBlank(message = "Movie title is required")
    private String movieTitle;

    private String movieDescription;
    private String movieGenre;
    private Integer movieDuration;
    private String movieRating;
    private String moviePosterUrl;

    @NotBlank(message = "Screening date/time is required")
    private String screenTime;

    private String hallName;
    private Integer totalRows;
    private Integer seatsPerRow;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;

    private Long movieId;
}
