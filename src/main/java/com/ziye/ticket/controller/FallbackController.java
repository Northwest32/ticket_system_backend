package com.ziye.ticket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import com.ziye.ticket.dto.CommonResponse;

@RestController
public class FallbackController {
    
    @GetMapping("/")
    public ResponseEntity<CommonResponse<String>> home() {
        System.out.println("Fallback: /");
        return ResponseEntity.ok(new CommonResponse<>(0, "Backend API is running", "Ticket System Backend"));
    }
    
    @GetMapping("/index.html")
    public ResponseEntity<CommonResponse<String>> index() {
        System.out.println("Fallback: /index.html");
        return ResponseEntity.ok(new CommonResponse<>(0, "Backend API is running", "Ticket System Backend"));
    }
    
    @GetMapping("/favicon.ico")
    public ResponseEntity<CommonResponse<String>> favicon() {
        System.out.println("Fallback: /favicon.ico");
        return ResponseEntity.ok(new CommonResponse<>(0, "Favicon requested", "No favicon configured"));
    }
    
    @GetMapping("/health")
    public ResponseEntity<CommonResponse<String>> health() {
        System.out.println("Fallback: /health");
        return ResponseEntity.ok(new CommonResponse<>(0, "Service is healthy", "Backend is running"));
    }
} 