package com.ziye.ticket.service;

import com.ziye.ticket.entity.Bookmark;
import com.ziye.ticket.dto.BookmarkDto;
import com.ziye.ticket.mapper.BookmarkMapper;
import com.ziye.ticket.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookmarkService {
    
    @Autowired
    private BookmarkMapper bookmarkMapper;
    
    @Autowired
    private EventMapper eventMapper;
    
    public boolean addBookmark(Long userId, Long eventId) {
        // check if already bookmarked
        Bookmark existingBookmark = bookmarkMapper.findByUserIdAndEventId(userId, eventId);
        if (existingBookmark != null) {
            return false; // already bookmarked
        }
        
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(userId);
        bookmark.setEventId(eventId);
        
        return bookmarkMapper.insertBookmark(bookmark) > 0;
    }
    
    public boolean removeBookmark(Long userId, Long eventId) {
        return bookmarkMapper.deleteBookmark(userId, eventId) > 0;
    }
    
    public List<Bookmark> getUserBookmarks(Long userId) {
        return bookmarkMapper.findByUserId(userId);
    }
    
    public List<BookmarkDto> getUserBookmarksWithEventDetails(Long userId) {
        List<Bookmark> bookmarks = bookmarkMapper.findByUserId(userId);
        
        return bookmarks.stream().map(bookmark -> {
            BookmarkDto dto = new BookmarkDto();
            dto.setId(bookmark.getId());
            dto.setUserId(bookmark.getUserId());
            dto.setEventId(bookmark.getEventId());
            
            // get event details
            try {
                var event = eventMapper.findById(bookmark.getEventId());
                if (event != null) {
                    dto.setEventTitle(event.getTitle());
                    dto.setEventDescription(event.getDescription());
                    dto.setEventLocation(event.getLocation());
                    dto.setEventDate(event.getEventDate());
                    dto.setEventPrice(event.getPrice());
                    dto.setEventImageUrl(event.getImageUrl());
                    dto.setOrganizerName(event.getOrganizerName());
                    dto.setOrganizerUsername(event.getOrganizerUsername());
                }
            } catch (Exception e) {
                // if failed to get event details, keep default value
                System.err.println("Failed to get event details for event " + bookmark.getEventId() + ": " + e.getMessage());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    public boolean isBookmarked(Long userId, Long eventId) {
        Bookmark bookmark = bookmarkMapper.findByUserIdAndEventId(userId, eventId);
        return bookmark != null;
    }
    
    public int getBookmarkCount(Long eventId) {
        return bookmarkMapper.countByEventId(eventId);
    }
} 