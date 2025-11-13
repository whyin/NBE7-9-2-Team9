package com.backend.domain.admin.service;


import com.backend.domain.category.dto.ResponseCategoryDto;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.category.service.CategoryService;
import com.backend.global.exception.BusinessException;
import com.backend.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    /** 전체 카테고리 조회 */
    public List<ResponseCategoryDto> getAllCategories() {
        return categoryService.findAll();
    }

    public ResponseCategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));
        return ResponseCategoryDto.from(category);
    }

    /** 카테고리 생성 */
    public ResponseCategoryDto createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        categoryRepository.save(category);
        return ResponseCategoryDto.from(category);
    }

    /** 카테고리 수정 */
    public ResponseCategoryDto updateCategory(Long categoryId, String newName) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));

        category.setName(newName);
        return ResponseCategoryDto.from(category);
    }

    /** 카테고리 삭제 */
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));

        categoryRepository.delete(category);
    }
}
