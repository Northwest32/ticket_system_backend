package com.ziye.ticket.service;

import com.ziye.ticket.entity.User;
import com.ziye.ticket.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String register(User user) {
        // check if email already exists
        User existingUserByEmail = userMapper.findByEmail(user.getEmail());
        if (existingUserByEmail != null) {
            return "Email already exists";
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insertUser(user);
        return "success";
    }

    public User login(String email, String rawPassword) {
        System.out.println("Login attempt for email: " + email);
        User user = userMapper.findByEmail(email);
        if (user == null) {
            System.out.println("User not found for email: " + email);
            return null;
        }
        System.out.println("User found: " + user.getUsername() + ", userType: " + user.getUserType());
        
        boolean passwordMatches = passwordEncoder.matches(rawPassword, user.getPassword());
        System.out.println("Password matches: " + passwordMatches);
        
        if (passwordMatches) {
            return user;
        }
        return null;
    }
} 