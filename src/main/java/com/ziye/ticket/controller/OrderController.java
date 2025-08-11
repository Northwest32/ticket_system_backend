package com.ziye.ticket.controller;

import com.ziye.ticket.entity.Order;
import com.ziye.ticket.service.OrderService;
import com.ziye.ticket.dto.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import io.jsonwebtoken.Claims;
import java.util.List;
import java.util.Map;
import com.ziye.ticket.util.JwtUtil;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private JwtUtil jwtUtil;

    // Buyer create the order
    @PostMapping
    public ResponseEntity<CommonResponse<String>> createOrder(@RequestBody Order order, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResponse<>(1, "Missing or invalid token", null));
        }
        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtUtil.parseToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResponse<>(1, "Invalid or expired token", null));
        }
        String userType = claims.get("userType", String.class);
        if (!"buyer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "Only buyer can create orders", null));
        }
        Long userId = Long.valueOf(claims.getSubject());
        order.setUserId(userId);
        int result = orderService.createOrder(order);
        if (result > 0) {
            return ResponseEntity.ok(new CommonResponse<>(0, "Order created successfully", "Order created successfully"));
        } else {
            return ResponseEntity.badRequest()
                .body(new CommonResponse<>(1, "Failed to create order", null));
        }
    }

    // Buyer view their own order list
    @GetMapping
    public ResponseEntity<CommonResponse<List<Order>>> getMyOrders(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResponse<>(1, "Missing or invalid token", null));
        }
        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtUtil.parseToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResponse<>(1, "Invalid or expired token", null));
        }
        String userType = claims.get("userType", String.class);
        if (!"buyer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "Only buyer can view orders", null));
        }
        Long userId = Long.valueOf(claims.getSubject());
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(new CommonResponse<>(0, "success", orders));
    }

    // Buyer view order details
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<Order>> getOrderById(@PathVariable Long id, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResponse<>(1, "Missing or invalid token", null));
        }
        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtUtil.parseToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResponse<>(1, "Invalid or expired token", null));
        }
        String userType = claims.get("userType", String.class);
        if (!"buyer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "Only buyer can view orders", null));
        }
        Long userId = Long.valueOf(claims.getSubject());
        Order order = orderService.getOrderById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "Order not found or access denied", null));
        }
        return ResponseEntity.ok(new CommonResponse<>(0, "success", order));
    }

    // Get all orders for a specific event (for organizers)
    @GetMapping("/event/{eventId}")
    public ResponseEntity<CommonResponse<List<Order>>> getOrdersByEventId(@PathVariable Long eventId, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResponse<>(1, "Missing or invalid token", null));
        }
        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtUtil.parseToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResponse<>(1, "Invalid or expired token", null));
        }
        String userType = claims.get("userType", String.class);
        if (!"organizer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "Only organizer can view event orders", null));
        }
        Long userId = Long.valueOf(claims.getSubject());
        
        List<Order> orders = orderService.getOrdersByEventId(eventId, userId);
        return ResponseEntity.ok(new CommonResponse<>(0, "success", orders));
    }

    // Update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<CommonResponse<String>> updateOrderStatus(@PathVariable Long id, 
                                                                   @RequestBody Map<String, String> request,
                                                                   HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResponse<>(1, "Missing or invalid token", null));
        }
        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtUtil.parseToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResponse<>(1, "Invalid or expired token", null));
        }
        String userType = claims.get("userType", String.class);
        if (!"organizer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "Only organizer can update order status", null));
        }
        
        String status = request.get("status");
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new CommonResponse<>(1, "Status is required", null));
        }
        
        int result = orderService.updateOrderStatus(id, status);
        if (result > 0) {
            return ResponseEntity.ok(new CommonResponse<>(0, "Order status updated successfully", "Order status updated"));
        } else {
            return ResponseEntity.badRequest()
                .body(new CommonResponse<>(1, "Failed to update order status", null));
        }
    }
} 