package com.ziye.ticket.controller;

import com.ziye.ticket.dto.CommonResponse;
import com.ziye.ticket.dto.RefundRequestDto;
import com.ziye.ticket.dto.RefundApprovalDto;
import com.ziye.ticket.entity.RefundRequest;
import com.ziye.ticket.service.RefundRequestService;
import com.ziye.ticket.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import io.jsonwebtoken.Claims;

import java.util.List;

@RestController
@RequestMapping("/api/refund-request")
public class RefundRequestController {
    
    @Autowired
    private RefundRequestService refundRequestService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // Buyer side: submit refund request
    @PostMapping
    public ResponseEntity<CommonResponse<RefundRequest>> createRefundRequest(
            @RequestBody RefundRequestDto dto,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            RefundRequest refundRequest = refundRequestService.createRefundRequest(dto, userId);
            return ResponseEntity.ok(new CommonResponse<>(0, "Refund request created successfully", refundRequest));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new CommonResponse<>(1, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonResponse<>(1, "Internal server error", null));
        }
    }
    
    // Buyer side: query my submitted refund requests
    @GetMapping("/my")
    public ResponseEntity<CommonResponse<List<RefundRequest>>> getMyRefundRequests(
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<RefundRequest> refundRequests = refundRequestService.getRefundRequestsByUserId(userId);
            return ResponseEntity.ok(new CommonResponse<>(0, "Success", refundRequests));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonResponse<>(1, "Internal server error", null));
        }
    }
    
    // Organizer side: query refund requests received for events I manage
    @GetMapping("/organizer")
    public ResponseEntity<CommonResponse<List<RefundRequest>>> getOrganizerRefundRequests(
            HttpServletRequest request) {
        try {
            Long organizerId = getUserIdFromToken(request);
            List<RefundRequest> refundRequests = refundRequestService.getRefundRequestsByOrganizerId(organizerId);
            return ResponseEntity.ok(new CommonResponse<>(0, "Success", refundRequests));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonResponse<>(1, "Internal server error", null));
        }
    }
    
    // Organizer side: approve refund request (approve)
    @PostMapping("/{id}/approve")
    public ResponseEntity<CommonResponse<RefundRequest>> approveRefundRequest(
            @PathVariable Long id,
            @RequestBody RefundApprovalDto dto,
            HttpServletRequest request) {
        try {
            Long organizerId = getUserIdFromToken(request);
            RefundRequest refundRequest = refundRequestService.approveRefundRequest(id, dto, organizerId);
            return ResponseEntity.ok(new CommonResponse<>(0, "Refund request approved", refundRequest));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new CommonResponse<>(1, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonResponse<>(1, "Internal server error", null));
        }
    }
    
    // Organizer side: approve refund request (reject)
    @PostMapping("/{id}/reject")
    public ResponseEntity<CommonResponse<RefundRequest>> rejectRefundRequest(
            @PathVariable Long id,
            @RequestBody RefundApprovalDto dto,
            HttpServletRequest request) {
        try {
            Long organizerId = getUserIdFromToken(request);
            RefundRequest refundRequest = refundRequestService.rejectRefundRequest(id, dto, organizerId);
            return ResponseEntity.ok(new CommonResponse<>(0, "Refund request rejected", refundRequest));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new CommonResponse<>(1, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonResponse<>(1, "Internal server error", null));
        }
    }
    
    // Query details of a specific refund request
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<RefundRequest>> getRefundRequestById(@PathVariable Long id) {
        try {
            RefundRequest refundRequest = refundRequestService.getRefundRequestById(id);
            if (refundRequest == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(new CommonResponse<>(0, "Success", refundRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonResponse<>(1, "Internal server error", null));
        }
    }
    
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalStateException("Missing or invalid token");
        }
        
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.parseToken(token);
        return Long.valueOf(claims.getSubject());
    }
} 