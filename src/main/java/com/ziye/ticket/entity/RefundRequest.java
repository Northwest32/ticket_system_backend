package com.ziye.ticket.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundRequest {
    private Long id;
    private Long orderId;       // orderId
    private Long userId;        // userId
    private Long organizerId;   // organizerId
    private String status;      // "REQUESTED", "APPROVED", "REJECTED"
    private String reason;      // reason (VARCHAR(255))
    private String reply;       // organizer reply (VARCHAR(255))
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
