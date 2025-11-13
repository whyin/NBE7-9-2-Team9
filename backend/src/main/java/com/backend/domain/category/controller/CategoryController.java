package com.backend.domain.category.controller;

import com.backend.domain.category.dto.ResponseCategoryDto;
import com.backend.domain.category.service.CategoryService;
import com.backend.global.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private  final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<ResponseCategoryDto>> getAllCategories(){
        List<ResponseCategoryDto> data = categoryService.findAll();
        return ApiResponse.success(data);
    }

}
