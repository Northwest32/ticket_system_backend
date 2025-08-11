package com.ziye.ticket.controller;

import com.ziye.ticket.entity.Event;
import com.ziye.ticket.service.EventService;
import com.ziye.ticket.dto.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import java.util.List;
import com.ziye.ticket.util.JwtUtil;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<CommonResponse<String>> createEvent(@Valid @RequestBody Event event, HttpServletRequest request) {
        System.out.println("🔍 Creating event with data: " + event);
        System.out.println("🔍 Event imageUrl: " + event.getImageUrl());
        System.out.println("🔍 Event capacity: " + event.getCapacity());
        System.out.println("🔍 Event remainingQuantity: " + event.getRemainingQuantity());
        
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
                .body(new CommonResponse<>(1, "Only organizer can create events", null));
        }
        Long organizerId = Long.valueOf(claims.getSubject());
        event.setCreatedBy(organizerId);
        event.setCreatedAt(LocalDateTime.now());
        event.setStatus("upcoming");
        
        System.out.println("🔍 Final event data before service call: " + event);
        System.out.println("🔍 Final capacity: " + event.getCapacity());
        System.out.println("🔍 Final remainingQuantity: " + event.getRemainingQuantity());
        
        int result = eventService.createEvent(event);
        if (result > 0) {
            return ResponseEntity.ok(new CommonResponse<>(0, "Event created successfully", "Event created successfully"));
        } else {
            return ResponseEntity.badRequest()
                .body(new CommonResponse<>(1, "Failed to create event", null));
        }
    }



    // View single event details
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<Event>> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CommonResponse<>(1, "Event not found", null));
        }
        return ResponseEntity.ok(new CommonResponse<>(0, "success", event));
    }

    // Buyer list events with optional filter
    @GetMapping
    public ResponseEntity<CommonResponse<List<Event>>> listEvents(
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        System.out.println("EventController: GET /api/events");
        
        List<Event> events;
        if (categoryId != null || keyword != null) {
            events = eventService.search(categoryId, keyword);
        } else {
            events = eventService.getEventsByPage(page, size);
        }
        
        System.out.println("Events: " + events.size() + " found");
        return ResponseEntity.ok(new CommonResponse<>(0, "success", events));
    }

    // Organizer view their own created event list
    @GetMapping("/my")
    public ResponseEntity<CommonResponse<List<Event>>> getMyEvents(HttpServletRequest request) {
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
                .body(new CommonResponse<>(1, "Only organizer can view their events", null));
        }
        Long organizerId = Long.valueOf(claims.getSubject());
        List<Event> events = eventService.getEventsByOrganizerId(organizerId);
        return ResponseEntity.ok(new CommonResponse<>(0, "success", events));
    }

    // Public interface: view events created by specified organizer
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<CommonResponse<List<Event>>> getEventsByOrganizer(@PathVariable Long organizerId) {
        List<Event> events = eventService.getEventsByOrganizerId(organizerId);
        return ResponseEntity.ok(new CommonResponse<>(0, "success", events));
    }
} 