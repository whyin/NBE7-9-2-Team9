package com.backend.domain.review.service;

import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.place.entity.Place;
import com.backend.domain.place.repository.PlaceRepository;
import com.backend.domain.review.dto.RecommendResponse;
import com.backend.domain.review.dto.ReviewRequestDto;
import com.backend.domain.review.dto.ReviewResponseDto;
import com.backend.domain.review.entity.Review;
import com.backend.domain.review.repository.ReviewRepository;
import com.backend.global.exception.BusinessException;
import com.backend.global.response.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final CategoryRepository categoryRepository;

    //리뷰 생성 메서드
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto reviewRequestDto, Long memberId) {
        long placeId = reviewRequestDto.placeId();

        Member member = getMemberEntity(memberId);
        Place place = getPlaceEntity(placeId);

        Optional<Review> check = reviewRepository.findByMemberIdAndPlaceId(member.getId(), place.getId());
        if(check.isPresent()){
            throw new BusinessException(ErrorCode.GIVEN_REVIEW);
        }
        Review review = new Review(place, member, reviewRequestDto.rating());
        review.onCreate();
        reviewRepository.save(review);

        return new ReviewResponseDto(member.getMemberId(), review.getId(), review.getRating(), review.getModifiedDate(), place.getCategory().getName(), place.getPlaceName(), place.getAddress(), place.getGu());
    }

    //리뷰 수정 메서드
    @Transactional
    public void modifyReview(Long memberId, int modifyRating){
//        Member member = getMemberEntity(memberId);
//        Review review = reviewRepository.findByMemberId(member.getId()).orElseThrow(
//                () -> new BusinessException(ErrorCode.NOT_FOUND_REVIEW)
//        );
        Review review = getReviewWithAuth(memberId);
        review.setRating(modifyRating);
        review.onUpdate();
    }

    //리뷰 삭제 메서드
    @Transactional
    public void deleteReview(Long memberId, long reviewId){
//        Review review = getReviewEntity(reviewId);
//        Member member = getMemberEntity(memberId);
//        if(member.getId() != review.getMember().getId()){           //본인 검증? 이정도면 괜찮을지 걱정..
//            throw new BusinessException(ErrorCode.ACCESS_DENIED);
//        }
        if(!validWithReviewId(memberId, reviewId)){
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        Review review = getReviewEntity(reviewId);
        reviewRepository.delete(review);
    }

    //내가 작성한 리뷰 조회
    public List<ReviewResponseDto> getMyReviews(Long memberId) {
//        Member member = getMemberEntity(memberId);
//        Review review = getReviewEntity(reviewId);
//        Review review = reviewRepository.findByMemberId(member.getId()).orElseThrow(
//                () -> new BusinessException(ErrorCode.NOT_FOUND_REVIEW)
//        );
        Review review = getReviewWithAuth(memberId);
        Member member = getMemberEntity(memberId);
        List<Review> myReviews = reviewRepository.findAllByMemberId(memberId);
        if(myReviews.isEmpty()){
            throw new BusinessException(ErrorCode.NOT_FOUND_REVIEW);
        }
        return myReviews.stream()
                .map(ReviewResponseDto::from)
                .toList();

    }

    //전체 리뷰 조회
    public List<ReviewResponseDto> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(ReviewResponseDto::from)
                .toList();
    }

    //여행지의 전체 리뷰 조회
    public List<ReviewResponseDto> getReviewList(Long placeId) {
        return reviewRepository.findByPlaceId(placeId)
                .stream()
                .map(ReviewResponseDto::from)
                .toList();
    }
    //"RESTAURANT", "LODGING", "NIGHTSPOT"
    public List<RecommendResponse> recommendHotel() {
        Map<Long, Double> placeAverageRatings = new HashMap<>();
        Category category = categoryRepository.findByName("HOTEL").orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY)
        );

        List<Map.Entry<Long, Double>> sortedList = getAllPlacesAndCalculate(category);
        List<RecommendResponse> recommendList = new ArrayList<>();
        for (long i = 0; i < 5 && i < sortedList.size(); i++) {       //여행지를 상위5개의 placeId를 가져와서 recommendList에 추가
            long recommendedPlaceId = sortedList.get((int) i).getKey();
            double averageRating = sortedList.get((int) i).getValue();
            Place place = getPlaceEntity(recommendedPlaceId);
            recommendList.add(RecommendResponse.from(place, averageRating));
        }
        return recommendList;

    }

    public List<RecommendResponse> recommendByPlace(Long placeId) {
        Map<Long, Double> placeAverageRatings = new HashMap<>(); //<placeId, averageRating> 으로 저장
//        long placeSize = placeRepository.count();               //중간에 값이 삭제되고나면 id가 건더뛰게 되는 상황은 어떻게 처리? -> findAll()으로 변경
        List<Place> findAllPlaces = placeRepository.findAll();
        for (Place place : findAllPlaces) {
            double averageRating = reviewRepository.findAverageRatingByPlaceId(place.getId());
            placeAverageRatings.put(place.getId(), averageRating);
        }

        //평균 평점 기준 내림차순 정렬 처음에 for 문으로 작성했다가 stream으로 변경했는데, 계속 에러떠서 결국 gpt의 도움을 받음.. 이런 방법은 괜찮은것인지?
        List<Map.Entry<Long, Double>> sortedList = placeAverageRatings.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed()) // 값 기준 내림차순
                .toList();

        List<RecommendResponse> recommendList = new ArrayList<>();
        for (long i = 0; i < 5 && i < sortedList.size(); i++) {       //여행지를 상위5개의 placeId를 가져와서 recommendList에 추가
            long recommendedPlaceId = sortedList.get((int) i).getKey();
            double averageRating = sortedList.get((int) i).getValue();
            Place place = getPlaceEntity(recommendedPlaceId);
            recommendList.add(RecommendResponse.from(place, averageRating));
        }
        return recommendList;
    }

    public Review getReviewEntity(Long reviewId){
        return reviewRepository.findById(reviewId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_REVIEW)
        );
    }

    public Place getPlaceEntity(Long placeId){
        return placeRepository.findById(placeId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_PLACE)
        );
    }

    public Member getMemberEntity(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND)
        );
    }

    public Review getReviewWithAuth(Long memberId){
        Member member = getMemberEntity(memberId);
        List<Review> reviews = reviewRepository.findAllByMemberId(member.getId());
        if (reviews.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_REVIEW);
        }

        return reviews.get(0);
    }

    public boolean validWithReviewId(Long memberId, Long reviewId){
        Review review = getReviewEntity(reviewId);
        Member member = getMemberEntity(memberId);
        return review.getMember().getId().equals(member.getId());
    }

    public List<RecommendResponse> recommendRestaurant() {
        Map<Long, Double> placeAverageRatings = new HashMap<>();
        Category category = categoryRepository.findByName("맛집").orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY)
        );

        List<Map.Entry<Long, Double>> sortedList = getAllPlacesAndCalculate(category);
        List<RecommendResponse> recommendList = new ArrayList<>();
        for (long i = 0; i < 5 && i < sortedList.size(); i++) {       //여행지를 상위5개의 placeId를 가져와서 recommendList에 추가
            long recommendedPlaceId = sortedList.get((int) i).getKey();
            double averageRating = sortedList.get((int) i).getValue();
            Place place = getPlaceEntity(recommendedPlaceId);
            recommendList.add(RecommendResponse.from(place, averageRating));
        }
        return recommendList;


    }

    public List<Map.Entry<Long, Double>> getAllPlacesAndCalculate(Category category) {
        Map<Long, Double> placeAverageRatings = new HashMap<>();
        List<Place> findAllPlaces = placeRepository.findByCategory_Name(category.getName());
        for(Place place : findAllPlaces){
            double averageRating = reviewRepository.findAverageRatingByPlaceId(place.getId());
            placeAverageRatings.put(place.getId(), averageRating);
        }
        List<Map.Entry<Long, Double>> sortedList = placeAverageRatings.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed()) // 값 기준 내림차순
                .toList();
        return sortedList;
    }

    public List<RecommendResponse> sortAllHotelReviews() {
        Map<Long, Double> placeAverageRatings = new HashMap<>();
        Category category = categoryRepository.findByName("HOTEL").orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY)
        );
        List<Map.Entry<Long, Double>> sortedList = getAllPlacesAndCalculate(category);
        List<RecommendResponse> responses = sortedList.stream()
                .map(entry -> {
                    Long placeId = entry.getKey();
                    Double avgRating = entry.getValue();

                    Place place = placeRepository.findById(placeId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));

                    return RecommendResponse.from(place, avgRating);
                })
                .toList();

        return responses;
    }
    public List<RecommendResponse> sortAllNightSpotReviews() {
        Map<Long, Double> placeAverageRatings = new HashMap<>();
        Category category = categoryRepository.findByName("NIGHTSPOT").orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY)
        );

        List<Map.Entry<Long, Double>> sortedList = getAllPlacesAndCalculate(category);
        List<RecommendResponse> responses = sortedList.stream()
                .map(entry -> {
                    Long placeId = entry.getKey();
                    Double avgRating = entry.getValue();

                    Place place = placeRepository.findById(placeId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));

                    return RecommendResponse.from(place, avgRating);
                })
                .toList();

        return responses;


    }
    public List<RecommendResponse> sortAllRestaurantReviews() {
        Map<Long, Double> placeAverageRatings = new HashMap<>();
        Category category = categoryRepository.findByName("맛집").orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY)
        );

        List<Map.Entry<Long, Double>> sortedList = getAllPlacesAndCalculate(category);
        List<RecommendResponse> responses = sortedList.stream()
                .map(entry -> {
                    Long placeId = entry.getKey();
                    Double avgRating = entry.getValue();

                    Place place = placeRepository.findById(placeId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));

                    return RecommendResponse.from(place, avgRating);
                })
                .toList();

        return responses;


    }

    public List<RecommendResponse> recommendNightSpot() {

        Map<Long, Double> placeAverageRatings = new HashMap<>();
        Category category = categoryRepository.findByName("NIGHTSPOT").orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY)
        );

        List<Map.Entry<Long, Double>> sortedList = getAllPlacesAndCalculate(category);

        List<RecommendResponse> recommendList = new ArrayList<>();
        for (long i = 0; i < 5 && i < sortedList.size(); i++) {       //여행지를 상위5개의 placeId를 가져와서 recommendList에 추가
            long recommendedPlaceId = sortedList.get((int) i).getKey();
            double averageRating = sortedList.get((int) i).getValue();
            Place place = getPlaceEntity(recommendedPlaceId);
            recommendList.add(RecommendResponse.from(place, averageRating));
        }
        return recommendList;
    }
}