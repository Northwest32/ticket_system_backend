package com.ziye.ticket.entity;

import lombok.Data;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private Long id;
    private Long orderId;
    private Double amount;
    private String paymentMethod; // ALIPAY / WECHAT / CARD etc.
    private String status; // PENDING, COMPLETED, CANCELLED, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}