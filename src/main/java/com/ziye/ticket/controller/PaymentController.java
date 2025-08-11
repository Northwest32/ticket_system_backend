package com.ziye.ticket.controller;

import com.ziye.ticket.dto.CommonResponse;
import com.ziye.ticket.dto.PaymentRequest;
import com.ziye.ticket.entity.Payment;
import com.ziye.ticket.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<CommonResponse<Payment>> create(@RequestBody PaymentRequest req) {
        Payment p = paymentService.createPayment(req);
        return ResponseEntity.ok(new CommonResponse<>(0, "created", p));
    }

    @PostMapping("/confirm/{orderId}")
    public ResponseEntity<CommonResponse<Payment>> confirm(@PathVariable Long orderId) {
        Payment p = paymentService.confirmPayment(orderId);
        return ResponseEntity.ok(new CommonResponse<>(0, "completed", p));
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<CommonResponse<Payment>> cancel(@PathVariable Long orderId) {
        Payment p = paymentService.cancelPayment(orderId);
        return ResponseEntity.ok(new CommonResponse<>(0, "cancelled", p));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<CommonResponse<Payment>> get(@PathVariable Long orderId) {
        Payment p = paymentService.getByOrderId(orderId);
        return ResponseEntity.ok(new CommonResponse<>(0, "ok", p));
    }
} 