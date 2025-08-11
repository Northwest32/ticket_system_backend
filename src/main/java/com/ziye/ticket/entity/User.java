package com.ziye.ticket.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String userType; // buyer / organizer
    private LocalDateTime createdAt;
    private String avatarUrl;
}
