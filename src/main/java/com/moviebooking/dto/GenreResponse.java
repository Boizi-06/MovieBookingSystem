package com.moviebooking.dto;

import com.moviebooking.entity.Genre;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreResponse {

    private Long id;
    private String name;
    private String description;

    public static GenreResponse fromEntity(Genre genre) {
        if (genre == null) return null;
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .description(genre.getDescription())
                .build();
    }
}
