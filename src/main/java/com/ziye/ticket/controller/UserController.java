package com.ziye.ticket.controller;

import com.ziye.ticket.dto.LoginRequest;
import com.ziye.ticket.dto.RegisterRequest;
import com.ziye.ticket.entity.User;
import com.ziye.ticket.service.UserService;
import com.ziye.ticket.util.JwtUtil;
import com.ziye.ticket.dto.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import io.jsonwebtoken.Claims;
import java.util.Map;
import java.util.HashMap;
import com.ziye.ticket.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.ziye.ticket.dto.ForgetPassword;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<String>> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setUserType(request.getUserType());
        
        String result = userService.register(user);
        if ("success".equals(result)) {
            return ResponseEntity.ok(new CommonResponse<>(0, "Register successful", "Register successful"));
        } else {
            return ResponseEntity.badRequest().body(new CommonResponse<>(1, result, null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<String>> login(@RequestBody LoginRequest request) {
        System.out.println("Login request received - Email: " + request.getEmail());
        User user = userService.login(request.getEmail(), request.getPassword());
        if (user == null) {
            System.out.println("Login failed - Invalid email or password");
            return ResponseEntity.badRequest().body(new CommonResponse<>(1, "Invalid email or password", null));
        }
        System.out.println("Login successful - User: " + user.getUsername());
        String token = jwtUtil.generateToken(user.getId(), user.getUserType(), user.getUsername());
        return ResponseEntity.ok(new CommonResponse<>(0, "Login successful", token));
    }

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<Map<String, Object>>> getProfile(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new CommonResponse<>(1, "Missing or invalid token", null));
    }

    String token = authHeader.substring(7); // Remove "Bearer "
    Claims claims;
    try {
        claims = jwtUtil.parseToken(token);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new CommonResponse<>(1, "Invalid or expired token", null));
    }

    Long userId = Long.valueOf(claims.getSubject());
    String userType = claims.get("userType", String.class);
    String username = claims.get("username", String.class);

    if (username == null || userType == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new CommonResponse<>(1, "Invalid token payload", null));
    }

    // Get complete user information
    User user = userMapper.findById(userId);
    
    Map<String, Object> result = new HashMap<>();
    result.put("id", userId);
    result.put("username", username);
    result.put("userType", userType);
    result.put("avatarUrl", user != null ? user.getAvatarUrl() : null);
    return ResponseEntity.ok(new CommonResponse<>(0, "success", result));
   }

    // Get user information by ID (public interface)
    @GetMapping("/users/{userId}")
    public ResponseEntity<CommonResponse<User>> getUserById(@PathVariable Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CommonResponse<>(1, "User not found", null));
        }
        
        // Do not return sensitive information
        user.setPassword(null);
        return ResponseEntity.ok(new CommonResponse<>(0, "success", user));
    }

   @PostMapping("/reset-password")
    public ResponseEntity<CommonResponse<String>> resetPassword(@RequestBody ForgetPassword dto) {
    User user = userMapper.findByEmail(dto.getEmail());
    if (user == null) {
        return ResponseEntity.badRequest().body(new CommonResponse<>(1, "Invalid email", null));
    }

    String encrypted = passwordEncoder.encode(dto.getNewPassword());
    userMapper.updatePasswordByEmail(dto.getEmail(), encrypted);
    return ResponseEntity.ok(new CommonResponse<>(0, "Password reset successfully", "Password reset successfully"));
}

   @PostMapping("/update-avatar")
    public ResponseEntity<CommonResponse<String>> updateAvatar(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        
        System.out.println("🔍 Update avatar request received");
        System.out.println("🔍 Request method: " + httpRequest.getMethod());
        System.out.println("🔍 Request URI: " + httpRequest.getRequestURI());
        
        String authHeader = httpRequest.getHeader("Authorization");
        System.out.println("🔍 Authorization header: " + authHeader);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Missing or invalid Authorization header");
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
        
        Long userId = Long.valueOf(claims.getSubject());
        
        // Get Cloudinary URL from request
        String cloudinaryUrl = request.get("avatarUrl");
        if (cloudinaryUrl == null || cloudinaryUrl.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new CommonResponse<>(1, "Avatar URL is required", null));
        }
        
        // Validate URL format (basic validation)
        if (!cloudinaryUrl.startsWith("https://res.cloudinary.com/")) {
            return ResponseEntity.badRequest().body(new CommonResponse<>(1, "Invalid Cloudinary URL format", null));
        }
        
        try {
            // Update user avatar URL with Cloudinary URL
            int result = userMapper.updateAvatarUrl(userId, cloudinaryUrl);
            
            if (result > 0) {
                return ResponseEntity.ok(new CommonResponse<>(0, "Avatar updated successfully", cloudinaryUrl));
            } else {
                return ResponseEntity.badRequest().body(new CommonResponse<>(1, "Failed to update avatar", null));
            }
        } catch (Exception e) {
            System.out.println("❌ Error updating avatar: " + e.getMessage());
            return ResponseEntity.badRequest().body(new CommonResponse<>(1, "Error updating avatar", null));
        }
    }

    // Test endpoint
    @GetMapping("/test-auth")
    public ResponseEntity<CommonResponse<String>> testAuth(HttpServletRequest request) {
        System.out.println("🔍 Test auth endpoint called!");
        String authHeader = request.getHeader("Authorization");
        System.out.println("🔍 Authorization header: " + authHeader);
        return ResponseEntity.ok(new CommonResponse<>(0, "Auth test successful", "Auth test successful"));
    }

    // Test file upload endpoint
    @PostMapping("/test-upload")
    public ResponseEntity<CommonResponse<String>> testUpload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        
        System.out.println("🔍 Test upload endpoint called!");
        System.out.println("🔍 File name: " + file.getOriginalFilename());
        System.out.println("🔍 File size: " + file.getSize());
        System.out.println("🔍 Content type: " + file.getContentType());
        
        String authHeader = request.getHeader("Authorization");
        System.out.println("🔍 Authorization header: " + authHeader);
        
        return ResponseEntity.ok(new CommonResponse<>(0, "Upload test successful", "File received: " + file.getOriginalFilename()));
    }

} 