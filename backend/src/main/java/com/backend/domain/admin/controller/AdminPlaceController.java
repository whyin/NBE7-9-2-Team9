package com.backend.domain.admin.controller;

import com.backend.domain.admin.service.AdminPlaceService;
import com.backend.domain.place.dto.RequestPlaceDto;
import com.backend.domain.place.dto.ResponsePlaceDto;
import com.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/places")
@RequiredArgsConstructor
public class AdminPlaceController {

    private final AdminPlaceService adminPlaceService;

    /** 전체 장소 조회 */
    @GetMapping
    public ApiResponse<List<ResponsePlaceDto>> getAllPlaces() {
        return ApiResponse.success(adminPlaceService.getAllPlaces());
    }

    /** 카테고리별 장소 조회 */
    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<ResponsePlaceDto>> getPlacesByCategory(@PathVariable int categoryId) {
        return ApiResponse.success(adminPlaceService.getPlacesByCategory(categoryId));
    }

    /** 장소 단건 조회 */
    @GetMapping("/{id}")
    public ApiResponse<ResponsePlaceDto> getPlace(@PathVariable Long id) {
        return ApiResponse.success(adminPlaceService.getPlace(id));
    }

    /** 장소 등록 */
    @PostMapping
    public ApiResponse<Void> createPlace(@RequestBody RequestPlaceDto dto) {
        adminPlaceService.createPlace(dto);
        return ApiResponse.success(null, "장소 등록 완료");
    }

    /** 장소 수정 */
    @PutMapping("/{id}")
    public ApiResponse<ResponsePlaceDto> updatePlace(@PathVariable Long id, @RequestBody RequestPlaceDto dto) {
        return ApiResponse.success(adminPlaceService.updatePlace(id, dto));
    }

    /** 장소 삭제 */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePlace(@PathVariable Long id) {
        adminPlaceService.deletePlace(id);
        return ApiResponse.success(null, "장소 삭제 완료");
    }
}
