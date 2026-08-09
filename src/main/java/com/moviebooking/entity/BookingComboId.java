package com.moviebooking.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingComboId implements Serializable {
    private Long bookingId;
    private Long comboId;
}
