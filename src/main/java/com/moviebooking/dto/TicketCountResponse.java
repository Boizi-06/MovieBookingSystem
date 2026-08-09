package com.moviebooking.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCountResponse {
    private String label; // YYYY-MM-DD, YYYY-MM, or YYYY
    private Long ticketCount;
}
