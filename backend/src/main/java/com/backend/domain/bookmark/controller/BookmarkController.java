package com.backend.domain.bookmark.controller;

import com.backend.domain.auth.service.AuthService;
import com.backend.domain.bookmark.dto.BookmarkRequestDto;
import com.backend.domain.bookmark.dto.BookmarkResponseDto;
import com.backend.domain.bookmark.service.BookmarkService;
import com.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final AuthService authService;

    /**
     * POST /api/bookmarks
     * body: { "placeId": 10 }
     */
    @PostMapping
    public ApiResponse<BookmarkResponseDto> create(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @Valid @RequestBody BookmarkRequestDto request) {

        Long memberId = authService.getMemberId(accessToken);

        BookmarkResponseDto response = bookmarkService.create(request, memberId);
        return ApiResponse.created(response);
    }

    /**
     * GET /api/bookmarks
     */
    @GetMapping
    public ApiResponse<List<BookmarkResponseDto>> list(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken
    ) {
        Long memberId = authService.getMemberId(accessToken);
        List<BookmarkResponseDto> list = bookmarkService.getList(memberId);
        return ApiResponse.success(list);
    }

    /**
     * DELETE /api/bookmarks/{bookmarkId}
     */
    @DeleteMapping("/{bookmarkId}")
    public ApiResponse<Long> delete(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @Valid @PathVariable Long bookmarkId) {

        Long memberId = authService.getMemberId(accessToken);
        bookmarkService.delete(memberId, bookmarkId);
        return ApiResponse.success(bookmarkId);
    }
}
