package com.backend.external.seoul.hotel.controller;

import com.backend.external.seoul.hotel.service.HotelImportService;
import com.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/import/hotels")
public class HotelImportController {

    private final HotelImportService hotelImportService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> importHotels() {
        int count = hotelImportService.importAll();
        return ResponseEntity.ok(ApiResponse.success(
                "호텔 " + count + "개 저장 완료"
        ));
    }
}