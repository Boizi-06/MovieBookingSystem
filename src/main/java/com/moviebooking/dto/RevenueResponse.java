package com.moviebooking.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueResponse {
    private String label; // YYYY-MM-DD, YYYY-MM, or YYYY
    private BigDecimal revenue;
}
