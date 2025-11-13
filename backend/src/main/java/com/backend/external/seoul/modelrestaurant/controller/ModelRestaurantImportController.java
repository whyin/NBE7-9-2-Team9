package com.backend.external.seoul.modelrestaurant.controller;

import com.backend.external.seoul.modelrestaurant.service.ModelRestaurantImportService;
import com.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/import/model-restaurants")
@RequiredArgsConstructor
public class ModelRestaurantImportController {

    private final ModelRestaurantImportService importService;

    // 모든 등록된 구 클라이언트 한번에
    @PostMapping
    public ResponseEntity<ApiResponse<Integer>> importAll() {
        int saved = importService.importAllDistricts();
        return ResponseEntity.ok(ApiResponse.success(saved, "모든 구 모범음식점 " + saved + "건 저장"));
    }

    // 특정 구만 (예: /internal/import/model-restaurants/ydp)
    @PostMapping("/{district}")
    public ResponseEntity<ApiResponse<Integer>> importOne(@PathVariable String district) {
        int saved = importService.importByDistrict(district);
        return ResponseEntity.ok(ApiResponse.success(saved, district + " 모범음식점 " + saved + "건 저장"));
    }
}