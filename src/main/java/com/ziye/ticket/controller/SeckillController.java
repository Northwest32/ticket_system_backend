package com.ziye.ticket.controller;

import com.ziye.ticket.service.SeckillService;
import com.ziye.ticket.dto.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import com.ziye.ticket.util.JwtUtil;

@RestController
@RequestMapping("/api")
public class SeckillController {
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/seckill/{eventId}")
    public ResponseEntity<?> seckill(@PathVariable Long eventId,
                                     @RequestParam(defaultValue = "1") int quantity,
                                     HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
        }
        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtUtil.parseToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        Long userId = Long.valueOf(claims.getSubject());

        Long result = seckillService.seckill(userId, eventId, quantity);
        if (result > 0) {
            // Success, return order ID
            return ResponseEntity.ok(new CommonResponse<>(0, "Seckill success", result));
        } else if (result == -1L) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sold out");
        } else if (result == -2L) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Purchase limit exceeded (max 3)");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Seckill failed");
        }
    }
} 