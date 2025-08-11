package com.ziye.ticket.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkDto {
    private Long id;
    private Long userId;
    private Long eventId;
    
    // event details
    private String eventTitle;
    private String eventDescription;
    private String eventLocation;
    private LocalDateTime eventDate;
    private Double eventPrice;
    private String eventImageUrl;
    private String organizerName;
    private String organizerUsername;
} 