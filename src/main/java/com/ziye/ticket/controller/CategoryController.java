package com.ziye.ticket.controller;

import com.ziye.ticket.entity.Category;
import com.ziye.ticket.mapper.CategoryMapper;
import com.ziye.ticket.dto.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping
    public CommonResponse<List<Category>> list() {
        return new CommonResponse<>(0, "success", categoryMapper.findAll());
    }
} 