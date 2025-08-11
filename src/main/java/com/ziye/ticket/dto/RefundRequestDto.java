package com.ziye.ticket.dto;

import lombok.Data;

@Data
public class RefundRequestDto {
    private Long orderId;
    private String reason;
} 