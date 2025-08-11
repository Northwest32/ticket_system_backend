package com.ziye.ticket.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private Long id;

    private String title;

    private String description; // support rich text HTML

    private String imageUrl; // cover image url

    private String location;

    private Long categoryId;
    private String categoryName; // add categoryName field

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventDate;

    private Double price;        // ticket price
    private Integer capacity;    // initial stock (total)
    private Integer remainingQuantity;

    private String status; // draft/upcoming/finished

    private Long createdBy;
    private String organizerName; // organizer name
    private String organizerUsername; // organizer username
    private String organizerAvatarUrl; // organizer avatar url
    private String organizerDescription; // organizer description

    private LocalDateTime createdAt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}