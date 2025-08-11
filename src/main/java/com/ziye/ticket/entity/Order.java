package com.ziye.ticket.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private Long userId;
    private Long eventId;
    private Integer quantity;
    private String status; // "PAID", "UNPAID", "CANCELLED" 
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    
    // related fields
    private String eventTitle;
    private String venue;
    private Double totalAmount;

    // refund request info
    private RefundRequest refundRequest;
}

