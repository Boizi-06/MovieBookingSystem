package com.moviebooking.dto;

import com.moviebooking.entity.Movie;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponse {

    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private LocalDate releaseDate;
    private LocalDate endDate;
    private String ageRating;
    private String language;
    private String director;
    private String cast;
    private String posterUrl;
    private String trailerUrl;
    private String status;
    private Set<GenreResponse> genres;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MovieResponse fromEntity(Movie movie) {
        if (movie == null) return null;

        Set<GenreResponse> genreResponses = (movie.getGenres() == null) ? Collections.emptySet() :
                movie.getGenres().stream()
                        .map(GenreResponse::fromEntity)
                        .collect(Collectors.toSet());

        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .releaseDate(movie.getReleaseDate())
                .endDate(movie.getEndDate())
                .ageRating(movie.getAgeRating())
                .language(movie.getLanguage())
                .director(movie.getDirector())
                .cast(movie.getCast())
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .status(movie.getStatus())
                .genres(genreResponses)
                .createdAt(movie.getCreatedAt())
                .updatedAt(movie.getUpdatedAt())
                .build();
    }
}
