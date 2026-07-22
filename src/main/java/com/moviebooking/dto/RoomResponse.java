package com.moviebooking.dto;

import com.moviebooking.entity.Room;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {

    private Long id;
    private Long cinemaId;
    private String cinemaName;
    private String name;
    private String roomType;
    private Integer totalSeats;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RoomResponse fromEntity(Room room) {
        if (room == null) return null;
        return RoomResponse.builder()
                .id(room.getId())
                .cinemaId(room.getCinema() != null ? room.getCinema().getId() : null)
                .cinemaName(room.getCinema() != null ? room.getCinema().getName() : null)
                .name(room.getName())
                .roomType(room.getRoomType())
                .totalSeats(room.getTotalSeats())
                .status(room.getStatus())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
