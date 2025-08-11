package com.ziye.ticket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizerProfile {
    private Long organizerId;
    private String homepageDescription;
    private String bannerImageUrl;
}
