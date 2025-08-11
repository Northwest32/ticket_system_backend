package com.ziye.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommonResponse<T> {
    private int code; // 0 success, 1 error
    private String msg;
    private T data;
} 