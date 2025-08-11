package com.ziye.ticket.controller;

import com.ziye.ticket.dto.CommonResponse;
import com.ziye.ticket.entity.OrganizerProfile;
import com.ziye.ticket.service.OrganizerProfileService;
import com.ziye.ticket.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/organizer-profile")
public class OrganizerProfileController {
    
    @Autowired
    private OrganizerProfileService organizerProfileService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/{organizerId}")
    public ResponseEntity<CommonResponse<OrganizerProfile>> getProfile(@PathVariable Long organizerId) {
        OrganizerProfile profile = organizerProfileService.getProfileByOrganizerId(organizerId);
        
        if (profile != null) {
            return ResponseEntity.ok(new CommonResponse<>(0, "Profile found", profile));
        } else {
            return ResponseEntity.ok(new CommonResponse<>(1, "Profile not found", null));
        }
    }
    
    @PostMapping("/save")
    public ResponseEntity<CommonResponse<String>> saveProfile(
            @RequestBody OrganizerProfile profile,
            HttpServletRequest request) {
        
        System.out.println("🔍 Received profile data: " + profile);
        
        // Verify token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Missing or invalid Authorization header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResponse<>(1, "Missing or invalid token", null));
        }
        
        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtUtil.parseToken(token);
            System.out.println("✅ Token parsed successfully. Claims: " + claims);
        } catch (Exception e) {
            System.out.println("❌ Token parsing failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResponse<>(1, "Invalid or expired token", null));
        }
        
        // Verify user permissions (can only edit own profile)
        Long userId = Long.valueOf(claims.getSubject());
        System.out.println("🔍 Token userId: " + userId + ", Profile organizerId: " + profile.getOrganizerId());
        System.out.println("🔍 Types - userId: " + userId.getClass() + ", organizerId: " + (profile.getOrganizerId() != null ? profile.getOrganizerId().getClass() : "null"));
        
        if (!userId.equals(profile.getOrganizerId())) {
            System.out.println("❌ Permission denied: userId != organizerId");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "You can only edit your own profile", null));
        }
        
        boolean success = organizerProfileService.saveOrUpdateProfile(profile);
        
        if (success) {
            System.out.println("✅ Profile saved successfully");
            return ResponseEntity.ok(new CommonResponse<>(0, "Profile saved successfully", "Profile saved successfully"));
        } else {
            System.out.println("❌ Failed to save profile");
            return ResponseEntity.badRequest().body(new CommonResponse<>(1, "Failed to save profile", null));
        }
    }
    
    @DeleteMapping("/{organizerId}")
    public ResponseEntity<CommonResponse<String>> deleteProfile(
            @PathVariable Long organizerId,
            HttpServletRequest request) {
        
        // Verify token
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
        
        // Verify user permissions
        Long userId = Long.valueOf(claims.getSubject());
        if (!userId.equals(organizerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "You can only delete your own profile", null));
        }
        
        boolean success = organizerProfileService.deleteProfile(organizerId);
        
        if (success) {
            return ResponseEntity.ok(new CommonResponse<>(0, "Profile deleted successfully", "Profile deleted successfully"));
        } else {
            return ResponseEntity.badRequest().body(new CommonResponse<>(1, "Failed to delete profile", null));
        }
    }
} 