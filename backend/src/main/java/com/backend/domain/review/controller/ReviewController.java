package com.backend.domain.review.controller;


import com.backend.domain.auth.service.AuthService;
import com.backend.domain.review.dto.RecommendResponse;
import com.backend.domain.review.dto.ReviewRequestDto;
import com.backend.domain.review.dto.ReviewResponseDto;
import com.backend.domain.review.service.ReviewService;
import com.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthService authService;

    //리뷰 등록
    @PostMapping("/add")
    public ApiResponse<ReviewResponseDto> createReview(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @RequestBody ReviewRequestDto reviewRequestDto
    ) {
        Long memberId = authService.getMemberId(accessToken);
        ReviewResponseDto createdReview = reviewService.createReview(reviewRequestDto, memberId);
        return ApiResponse.created(createdReview);
    }

    //리뷰 수정
    @PatchMapping("/modify/{memberId}")
    public ApiResponse<Void> modifyReview(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @PathVariable long memberId, @RequestParam int modifyRating) {
        Long authMemberId = authService.getMemberId(accessToken);
        reviewService.modifyReview(authMemberId, modifyRating);
        return ApiResponse.success();
    }

    //리뷰 삭제
    @DeleteMapping("/delete/{reviewId}")
    public ApiResponse<Void> deleteReview(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @PathVariable long reviewId) {
        Long memberId = authService.getMemberId(accessToken);
        reviewService.deleteReview(memberId, reviewId);
        return ApiResponse.success();
    }

    // 내가 작성한 리뷰 조회
    @GetMapping("/myReview") // 꼭 reviewId가 필요한가?
    public ApiResponse<List<ReviewResponseDto>> getMyReview(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken) {
        Long memberId = authService.getMemberId(accessToken);
        List<ReviewResponseDto> response = reviewService.getMyReviews(memberId);
//        ReviewResponseDto response = reviewService.getReview(memberId, reviewId);
        return ApiResponse.success(response);
    }

    // 특정 여행지의 리뷰 조회
    @GetMapping("/list/{placeId}")
    public ApiResponse<List<ReviewResponseDto>> getPlaceReview(@PathVariable long placeId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewList(placeId);
        return ApiResponse.success(reviews);
    }

    // 전체 리뷰 조회
    @GetMapping("/lists")
    public ApiResponse<List<ReviewResponseDto>> getAllReview() {
        List<ReviewResponseDto> reviews = reviewService.getAllReviews();
        return ApiResponse.success(reviews);
    }

    //추천리뷰 -> 평균 별점 상위 5개의 여행지를 추천
    @GetMapping("/recommend/{placeId}")
    public ApiResponse<List<RecommendResponse>> getRecommendedReviews(@PathVariable long placeId){
        List<RecommendResponse> recommendedPlaces = reviewService.recommendByPlace(placeId);
        return ApiResponse.success(recommendedPlaces);
    }
    //카테고리 - 호텔
    @GetMapping("/recommend/hotel")
    public ApiResponse<List<RecommendResponse>> recommendHotelReviews(){
        List<RecommendResponse> recommendedPlaces = reviewService.recommendHotel();
        return ApiResponse.success(recommendedPlaces);
    }
    //카테고리 - 맛집
    @GetMapping("/recommend/restaurant")
    public ApiResponse<List<RecommendResponse>> recommendRestaurantReviews(){
        List<RecommendResponse> recommendedPlaces = reviewService.recommendRestaurant();
        return ApiResponse.success(recommendedPlaces);
    }
    //카테고리 - 야경
    @GetMapping("/recommend/nightspot")
    public ApiResponse<List<RecommendResponse>> recommendNightspotReviews(){
        List<RecommendResponse> recommendedPlaces = reviewService.recommendNightSpot();
        return ApiResponse.success(recommendedPlaces);
    }

    //카테고리 - 호텔
    @GetMapping("/recommend/allHotel")
    public ApiResponse<List<RecommendResponse>> sortAllHotelReviews(){
        List<RecommendResponse> recommendedPlaces = reviewService.sortAllHotelReviews();
        return ApiResponse.success(recommendedPlaces);
    }
    //카테고리 - 맛집
    @GetMapping("/recommend/allRestaurant")
    public ApiResponse<List<RecommendResponse>> sortAllRestaurantReviews(){
        List<RecommendResponse> recommendedPlaces = reviewService.sortAllRestaurantReviews();
        return ApiResponse.success(recommendedPlaces);
    }
    //카테고리 - 야경
    @GetMapping("/recommend/allNightspot")
    public ApiResponse<List<RecommendResponse>> sortAllNightspotReviews(){
        List<RecommendResponse> recommendedPlaces = reviewService.sortAllNightSpotReviews();
        return ApiResponse.success(recommendedPlaces);
    }

}
