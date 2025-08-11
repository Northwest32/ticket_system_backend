package com.ziye.ticket.controller;

import com.ziye.ticket.entity.Comment;
import com.ziye.ticket.dto.CommonResponse;
import com.ziye.ticket.service.CommentService;
import com.ziye.ticket.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    @Autowired
    private CommentService service;
    
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/comments")
    public ResponseEntity<CommonResponse<Comment>> add(@RequestBody Comment c, HttpServletRequest request) {
        // Verify user authentication
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
        
        // Verify user identity
        Long userId = Long.valueOf(claims.getSubject());
        if (!userId.equals(c.getFromUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "You can only comment as yourself", null));
        }
        
        // Add comment
        Comment addedComment = service.add(c);
        return ResponseEntity.ok(new CommonResponse<>(0, "Comment added successfully", addedComment));
    }

    @GetMapping("/events/{eid}/comments")
    public CommonResponse<List<Comment>> topEvent(@PathVariable Long eid){
        return new CommonResponse<>(0, "success", service.topByEvent(eid));
    }

    @GetMapping("/organizers/{oid}/comments")
    public CommonResponse<List<Comment>> topOrg(@PathVariable Long oid){
        return new CommonResponse<>(0, "success", service.topByOrganizer(oid));
    }

    @GetMapping("/comments/{cid}/replies")
    public CommonResponse<List<Comment>> replies(@PathVariable Long cid){
        return new CommonResponse<>(0, "success", service.replies(cid));
    }

    @GetMapping("/users/{uid}/comments/received")
    public CommonResponse<List<Comment>> recv(@PathVariable Long uid){
        return new CommonResponse<>(0, "success", service.received(uid));
    }

    @GetMapping("/users/{uid}/comments/given")
    public CommonResponse<List<Comment>> given(@PathVariable Long uid){
        return new CommonResponse<>(0, "success", service.given(uid));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommonResponse<Boolean>> deleteComment(@PathVariable Long commentId, @RequestParam Long userId, HttpServletRequest request) {
        // Verify user authentication
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
        
        // Verify user identity
        Long tokenUserId = Long.valueOf(claims.getSubject());
        if (!tokenUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new CommonResponse<>(1, "You can only delete your own comments", null));
        }
        
        boolean result = service.deleteComment(commentId, userId);
        if (result) {
            return ResponseEntity.ok(new CommonResponse<>(0, "Comment deleted successfully", true));
        } else {
            return ResponseEntity.badRequest()
                .body(new CommonResponse<>(1, "Failed to delete comment. Comment not found or not authorized.", false));
        }
    }
} 