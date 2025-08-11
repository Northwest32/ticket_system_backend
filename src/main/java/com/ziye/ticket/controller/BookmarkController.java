package com.ziye.ticket.controller;

import com.ziye.ticket.dto.BookmarkRequest;
import com.ziye.ticket.dto.BookmarkDto;
import com.ziye.ticket.entity.Bookmark;
import com.ziye.ticket.service.BookmarkService;
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
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    
    @Autowired
    private BookmarkService bookmarkService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // Add bookmark
    @PostMapping
    public ResponseEntity<?> addBookmark(@Valid @RequestBody BookmarkRequest request, HttpServletRequest httpRequest) {
        // Verify user identity
        String authHeader = httpRequest.getHeader("Authorization");
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
        
        // Check user type, only buyer can bookmark
        String userType = claims.get("userType", String.class);
        if (!"buyer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only buyer can bookmark events");
        }
        
        Long userId = Long.valueOf(claims.getSubject());
        boolean success = bookmarkService.addBookmark(userId, request.getEventId());
        
        if (success) {
            return ResponseEntity.ok("Event bookmarked successfully");
        } else {
            return ResponseEntity.badRequest().body("Event already bookmarked or failed to bookmark");
        }
    }
    
    // Remove bookmark
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> removeBookmark(@PathVariable Long eventId, HttpServletRequest httpRequest) {
        // Verify user identity
        String authHeader = httpRequest.getHeader("Authorization");
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
        
        // Check user type, only buyer can remove bookmarks
        String userType = claims.get("userType", String.class);
        if (!"buyer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only buyer can remove bookmarks");
        }
        
        Long userId = Long.valueOf(claims.getSubject());
        boolean success = bookmarkService.removeBookmark(userId, eventId);
        
        if (success) {
            return ResponseEntity.ok("Bookmark removed successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to remove bookmark");
        }
    }
    
    // Get user's bookmark list
    @GetMapping("/my")
    public ResponseEntity<CommonResponse<List<Bookmark>>> getMyBookmarks(HttpServletRequest httpRequest) {
        // Verify user identity
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
        
        // Check user type, only buyer can view bookmarks
        String userType = claims.get("userType", String.class);
        if (!"buyer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "Only buyer can view bookmarks", null));
        }
        
        Long userId = Long.valueOf(claims.getSubject());
        List<Bookmark> bookmarks = bookmarkService.getUserBookmarks(userId);
        return ResponseEntity.ok(new CommonResponse<>(0, "success", bookmarks));
    }
    
    // get user's bookmark list
    @GetMapping("/my/with-details")
    public ResponseEntity<CommonResponse<List<BookmarkDto>>> getMyBookmarksWithDetails(HttpServletRequest httpRequest) {
        // authorize the identity
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
        
        // check the type, only buyer can view bookmark
        String userType = claims.get("userType", String.class);
        if (!"buyer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "Only buyer can view bookmarks", null));
        }
        
        Long userId = Long.valueOf(claims.getSubject());
        List<BookmarkDto> bookmarks = bookmarkService.getUserBookmarksWithEventDetails(userId);
        return ResponseEntity.ok(new CommonResponse<>(0, "success", bookmarks));
    }
    
    // check if the user has bookmarked the event
    @GetMapping("/check/{eventId}")
    public ResponseEntity<?> checkBookmark(@PathVariable Long eventId, HttpServletRequest httpRequest) {
        // verify the identity
        String authHeader = httpRequest.getHeader("Authorization");
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
        
        // check the type, only buyer can check the bookmark status
        String userType = claims.get("userType", String.class);
        if (!"buyer".equals(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only buyer can check bookmark status");
        }
        
        Long userId = Long.valueOf(claims.getSubject());
        boolean isBookmarked = bookmarkService.isBookmarked(userId, eventId);
        return ResponseEntity.ok(isBookmarked);
    }
    
    // get the bookmark count of the event
    @GetMapping("/count/{eventId}")
    public ResponseEntity<?> getBookmarkCount(@PathVariable Long eventId) {
        int count = bookmarkService.getBookmarkCount(eventId);
        return ResponseEntity.ok(count);
    }
} 