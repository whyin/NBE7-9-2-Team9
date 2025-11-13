package com.backend.domain.place.controller;

import com.backend.domain.place.dto.RequestPlaceDto;
import com.backend.domain.place.dto.ResponsePlaceDto;
import com.backend.domain.place.service.PlaceService;
import com.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/place")
@RequiredArgsConstructor
@Validated
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<ResponsePlaceDto>> getPlacesByCategoryId(@PathVariable @Min(1) int categoryId) {
        List<ResponsePlaceDto> data = placeService.findPlacesByCategoryId(categoryId);
        return ApiResponse.success(data);
    }

    @GetMapping("/{id}")
    public ApiResponse<ResponsePlaceDto> getPlace(@PathVariable @Min(1) Long id) {
        ResponsePlaceDto data = placeService.findOnePlace(id);
        return ApiResponse.success(data);
    }

    @PostMapping
    public ApiResponse<Void> createPlace(@RequestBody @Valid RequestPlaceDto dto) {
        placeService.save(dto);
        return ApiResponse.created(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<ResponsePlaceDto> updatePlace(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid RequestPlaceDto dto
    ) {
        ResponsePlaceDto updated = placeService.update(id, dto);
        return ApiResponse.success(updated, "여행지가 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePlace(@PathVariable @Min(1) Long id) {
        placeService.delete(id);
        return ApiResponse.success(null, "여행지가 삭제되었습니다.");
    }

}
