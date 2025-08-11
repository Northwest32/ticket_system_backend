package com.ziye.ticket.controller;

import com.ziye.ticket.dto.FollowRequest;
import com.ziye.ticket.entity.Follow;
import com.ziye.ticket.service.FollowService;
import com.ziye.ticket.util.JwtUtil;
import com.ziye.ticket.dto.CommonResponse;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/follows")
public class FollowController {
    
    @Autowired
    private FollowService followService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // follow organizer
    @PostMapping
    public ResponseEntity<CommonResponse<String>> addFollow(@Valid @RequestBody FollowRequest request, HttpServletRequest httpRequest) {
        // verify the identity
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
        
        // check the type, only buyer can follow
        String userType = claims.get("userType", String.class);
        if (!"buyer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "Only buyer can follow organizers", null));
        }
        
        Long userId = Long.valueOf(claims.getSubject());
        boolean success = followService.addFollow(userId, request.getOrganizerId());
        
        if (success) {
            return ResponseEntity.ok(new CommonResponse<>(0, "Organizer followed successfully", "success"));
        } else {
            return ResponseEntity.badRequest()
                .body(new CommonResponse<>(1, "Organizer already followed or failed to follow", null));
        }
    }
    
    // remove follow
    @DeleteMapping("/{organizerId}")
    public ResponseEntity<CommonResponse<String>> removeFollow(@PathVariable Long organizerId, HttpServletRequest httpRequest) {
        // verify the identity
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
        
        // check the type, only buyer can unfollow
        String userType = claims.get("userType", String.class);
        if (!"buyer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "Only buyer can unfollow organizers", null));
        }
        
        Long userId = Long.valueOf(claims.getSubject());
        boolean success = followService.removeFollow(userId, organizerId);
        
        if (success) {
            return ResponseEntity.ok(new CommonResponse<>(0, "Follow removed successfully", "success"));
        } else {
            return ResponseEntity.badRequest()
                .body(new CommonResponse<>(1, "Failed to remove follow", null));
        }
    }
    
    // get user's follow list
    @GetMapping("/my")
    public ResponseEntity<CommonResponse<List<Follow>>> getMyFollows(HttpServletRequest httpRequest) {
        // verify the identity
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
        
        // check the type, only buyer can view follow
        String userType = claims.get("userType", String.class);
        if (!"buyer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "Only buyer can view follows", null));
        }
        
        Long userId = Long.valueOf(claims.getSubject());
        List<Follow> follows = followService.getUserFollows(userId);
        return ResponseEntity.ok(new CommonResponse<>(0, "success", follows));
    }
    
    // check if the user has followed the organizer
    @GetMapping("/check/{organizerId}")
    public ResponseEntity<CommonResponse<Boolean>> checkFollow(@PathVariable Long organizerId, HttpServletRequest httpRequest) {
        // verify the identity
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
        
        // check the type, only buyer can check the follow status
        String userType = claims.get("userType", String.class);
        if (!"buyer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "Only buyer can check follow status", null));
        }
        
        Long userId = Long.valueOf(claims.getSubject());
        boolean isFollowing = followService.isFollowing(userId, organizerId);
        return ResponseEntity.ok(new CommonResponse<>(0, "success", isFollowing));
    }
    
    // get the follower count of the organizer
    @GetMapping("/count/{organizerId}")
    public ResponseEntity<CommonResponse<Integer>> getFollowerCount(@PathVariable Long organizerId) {
        int count = followService.getFollowerCount(organizerId);
        return ResponseEntity.ok(new CommonResponse<>(0, "success", count));
    }
} 