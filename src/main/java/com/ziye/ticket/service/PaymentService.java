package com.ziye.ticket.service;

import com.ziye.ticket.dto.PaymentRequest;
import com.ziye.ticket.entity.Payment;
import com.ziye.ticket.entity.Order;
import com.ziye.ticket.mapper.PaymentMapper;
import com.ziye.ticket.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaymentService {
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public Payment createPayment(PaymentRequest req) {
        Order order = orderMapper.findById(req.getOrderId());
        if (order == null || !"UNPAID".equals(order.getStatus())) {
            throw new IllegalStateException("Order not exist or already paid");
        }
        Payment p = new Payment();
        p.setOrderId(req.getOrderId());
        p.setAmount(req.getAmount());
        p.setPaymentMethod(req.getPaymentMethod());
        p.setStatus("PENDING");
        paymentMapper.insert(p);
        return p;
    }

    @Transactional
    public Payment confirmPayment(Long orderId) {
        Payment p = paymentMapper.findByOrderId(orderId);
        if (p == null) throw new IllegalStateException("Payment not found");
        paymentMapper.updateStatus(p.getId(), "COMPLETED");
        orderMapper.updateStatus(orderId, "PAID");
        p.setStatus("COMPLETED");
        return p;
    }

    @Transactional
    public Payment cancelPayment(Long orderId) {
        Payment p = paymentMapper.findByOrderId(orderId);
        if (p == null) throw new IllegalStateException("Payment not found");
        paymentMapper.updateStatus(p.getId(), "CANCELLED");
        p.setStatus("CANCELLED");
        return p;
    }

    public Payment getByOrderId(Long orderId) {
        return paymentMapper.findByOrderId(orderId);
    }
} 