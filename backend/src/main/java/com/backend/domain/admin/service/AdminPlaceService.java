package com.backend.domain.admin.service;

import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.place.dto.RequestPlaceDto;
import com.backend.domain.place.dto.ResponsePlaceDto;
import com.backend.domain.place.entity.Place;
import com.backend.domain.place.repository.PlaceRepository;
import com.backend.domain.place.service.PlaceService;
import com.backend.global.exception.BusinessException;
import com.backend.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminPlaceService {
    private final PlaceRepository placeRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceService placeService; // 기존 로직 재사용

    /** 전체 장소 조회 */
    public List<ResponsePlaceDto> getAllPlaces() {
        return placeRepository.findAll()
                .stream()
                .map(ResponsePlaceDto::from)
                .toList();
    }

    /** 특정 카테고리에 속한 장소 조회 */
    public List<ResponsePlaceDto> getPlacesByCategory(int categoryId) {
        return placeService.findPlacesByCategoryId(categoryId);
    }

    /** 장소 단건 조회 */
    public ResponsePlaceDto getPlace(Long id) {
        return placeService.findOnePlace(id);
    }

    /** 장소 등록 (관리자 직접 등록) */
    public void createPlace(RequestPlaceDto dto) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));

        Place place = Place.builder()
                .placeName(dto.placeName())
                .address(dto.address())
                .gu(dto.gu())
                .description(dto.description())
                .category(category)
                .build();

        placeRepository.save(place);
    }

    /** 장소 수정 */
    public ResponsePlaceDto updatePlace(Long id, RequestPlaceDto dto) {
        return placeService.update(id, dto); // 기존 로직 재사용
    }

    /** 장소 삭제 (강제 삭제 포함) */
    public void deletePlace(Long id) {
        placeRepository.delete(
                placeRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE))
        );
    }
}
