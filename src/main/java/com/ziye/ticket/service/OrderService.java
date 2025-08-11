package com.ziye.ticket.service;

import com.ziye.ticket.entity.Order;
import com.ziye.ticket.entity.RefundRequest;
import com.ziye.ticket.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;

    public int createOrder(Order order) {
        return orderMapper.insertOrder(order);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    public Order getOrderById(Long id) {
        return orderMapper.findById(id);
    }

    public List<Order> getOrdersByEventId(Long eventId, Long organizerId) {
        List<Order> orders = orderMapper.findByEventId(eventId);
        
        // load refund request info for each order
        for (Order order : orders) {
            RefundRequest refundRequest = orderMapper.findRefundRequestByOrderId(order.getId());
            order.setRefundRequest(refundRequest);
        }
        
        return orders;
    }

    public int updateOrderStatus(Long id, String status) {
        return orderMapper.updateStatus(id, status);
    }
} 