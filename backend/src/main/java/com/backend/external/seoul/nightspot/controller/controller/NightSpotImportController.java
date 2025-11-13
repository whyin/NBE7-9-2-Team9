// NightSpotImportController.java
package com.backend.external.seoul.nightspot.controller.controller;

import com.backend.external.seoul.nightspot.service.service.NightSpotImportService;
import com.backend.global.response.ApiResponse;
import com.backend.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/import/nightspots")
public class NightSpotImportController {

    private final NightSpotImportService importService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> importNightSpots() {
        int saved = importService.importAll();

        // 201 Created로 응답 (바디는 커스텀 메시지)
        ApiResponse<Void> body = new ApiResponse<>(
                ResponseCode.CREATED.getCode(),
                "야간명소 " + saved + "건 저장 완료",
                null
        );
        return ResponseEntity
                .status(ResponseCode.CREATED.getStatus())
                .body(body);
    }
}