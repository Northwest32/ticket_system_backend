package com.ziye.ticket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Follow {
    private Long id;
    private Long userId;       
    private Long organizerId;
    
    // extra fields for frontend display
    private String organizerName;
    private Integer organizerEventCount;
    private Integer followerCount;
}
