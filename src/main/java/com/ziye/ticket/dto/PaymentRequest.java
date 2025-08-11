package com.ziye.ticket.dto;

import lombok.Data;
import java.util.Map;

@Data
public class PaymentRequest {
    private Long orderId;
    private Double amount;
    private String paymentMethod;

    private Map<String, String> extraInfo; //extra info for payment method
} 