package com.ziye.ticket.dto;

import lombok.Data;

@Data
public class ForgetPassword {
    private String email;
    private String newPassword;
}
