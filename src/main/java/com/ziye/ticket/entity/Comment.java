package com.ziye.ticket.entity;

import java.util.Date;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Long id;
    private String content;
    private Long fromUserId;
    private String fromUserName; // commenter username
    private String fromUserType; // commenter user type
    private Boolean hasPurchased; // whether the commenter has purchased the event
    private Long toEventId;
    private Long toOrganizerId;
    private Integer rating;
    private Date createdAt;
    private Long parentCommentId;
}
