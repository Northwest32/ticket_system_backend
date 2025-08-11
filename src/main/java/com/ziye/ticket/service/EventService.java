package com.ziye.ticket.service;

import com.ziye.ticket.entity.Event;
import com.ziye.ticket.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public int createEvent(Event event) {
        System.out.println("🔍 EventService.createEvent called with event: " + event);
        System.out.println("🔍 EventService - imageUrl: " + event.getImageUrl());
        System.out.println("🔍 EventService - capacity: " + event.getCapacity());
        System.out.println("🔍 EventService - remainingQuantity: " + event.getRemainingQuantity());
        
        // set remaining quantity = capacity (if null, set to 0)
        if (event.getCapacity() != null) {
            event.setRemainingQuantity(event.getCapacity());
        } else {
            event.setRemainingQuantity(0);
        }
        
        System.out.println("🔍 EventService - after setting remainingQuantity: " + event.getRemainingQuantity());
        
        int rows = eventMapper.insertEvent(event);
        System.out.println("🔍 EventService - insertEvent result: " + rows);
        
        // initialize Redis stock key after successful insertion
        if (rows > 0) {
            String key = "event:stock:" + event.getId();
            redisTemplate.opsForValue().set(key, String.valueOf(event.getRemainingQuantity()));
            System.out.println("🔍 EventService - Redis key set: " + key + " = " + event.getRemainingQuantity());
        }
        return rows;
    }

    public List<Event> getAllEvents() {
        List<Event> events = eventMapper.findAll();
        return processImageUrls(events);
    }

    public Event getEventById(Long id) {
        Event event = eventMapper.findById(id);
        if (event != null && event.getImageUrl() != null && !event.getImageUrl().startsWith("http")) {
            event.setImageUrl(baseUrl + event.getImageUrl());
        }
        return event;
    }

    public List<Event> getEventsByPage(int page, int size) {
        int offset = (page - 1) * size;
        List<Event> events = eventMapper.findByPage(offset, size);
        return processImageUrls(events);
    }

    public List<Event> getEventsByOrganizerId(Long organizerId) {
        List<Event> events = eventMapper.findByOrganizerId(organizerId);
        return processImageUrls(events);
    }

    public List<Event> search(Long categoryId, String keyword) {
        List<Event> events = eventMapper.searchEvents(categoryId, keyword);
        return processImageUrls(events);
    }
    
    private List<Event> processImageUrls(List<Event> events) {
        for (Event event : events) {
            if (event.getImageUrl() != null && !event.getImageUrl().startsWith("http")) {
                // if relative path, add base URL
                event.setImageUrl(baseUrl + event.getImageUrl());
            }
        }
        return events;
    }
} 