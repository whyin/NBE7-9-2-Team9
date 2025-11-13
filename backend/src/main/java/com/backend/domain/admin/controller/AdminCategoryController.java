package com.backend.domain.admin.controller;

import com.backend.domain.admin.service.AdminCategoryService;
import com.backend.domain.category.dto.ResponseCategoryDto;
import com.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    /** 전체 카테고리 조회 */
    @GetMapping
    public ApiResponse<List<ResponseCategoryDto>> getAllCategories() {
        return ApiResponse.success(adminCategoryService.getAllCategories());
    }

    /** 카테고리 단건 조회 */
    @GetMapping("/{categoryId}")
    public ApiResponse<ResponseCategoryDto> getCategoryById(@PathVariable Long categoryId) {
        return ApiResponse.success(adminCategoryService.getCategoryById(categoryId));
    }

    /** 카테고리 생성 */
    @PostMapping
    public ApiResponse<ResponseCategoryDto> createCategory(@RequestBody String name) {
        return ApiResponse.success(adminCategoryService.createCategory(name));
    }

    /** 카테고리 수정 */
    @PutMapping("/{categoryId}")
    public ApiResponse<ResponseCategoryDto> updateCategory(
            @PathVariable Long categoryId,
            @RequestParam String newName
    ) {
        return ApiResponse.success(adminCategoryService.updateCategory(categoryId, newName));
    }

    /** 카테고리 삭제 */
    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long categoryId) {
        adminCategoryService.deleteCategory(categoryId);
        return ApiResponse.success(null, "카테고리 삭제 완료");
    }
}
