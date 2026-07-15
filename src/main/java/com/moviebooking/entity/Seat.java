package com.moviebooking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "seat_row", nullable = false, length = 5)
    private String seatRow;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "seat_code", nullable = false, length = 10)
    private String seatCode;

    @Column(name = "seat_type", nullable = false, length = 20)
    private String seatType; // "STANDARD", "VIP", "COUPLE"

    @Column(name = "price_multiplier", nullable = false, precision = 3, scale = 2)
    private BigDecimal priceMultiplier;

    @Column(nullable = false, length = 20)
    private String status; // "ACTIVE", "MAINTENANCE", "INACTIVE"
}
