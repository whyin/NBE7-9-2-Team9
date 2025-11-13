package com.backend.domain.bookmark.service;

import com.backend.domain.bookmark.dto.BookmarkRequestDto;
import com.backend.domain.bookmark.dto.BookmarkResponseDto;
import com.backend.domain.bookmark.entity.Bookmark;
import com.backend.domain.bookmark.repository.BookmarkRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.service.MemberService;
import com.backend.domain.place.entity.Place;
import com.backend.domain.place.service.PlaceService;
import com.backend.global.exception.BusinessException;
import com.backend.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberService memberService;
    private final PlaceService placeService;

    /**
     * 북마크 생성
     * - 이미 소프트 삭제된 엔티티가 있으면 재활성화(삭제일 제거, createdAt 갱신)
     */
    @Transactional
    public BookmarkResponseDto create(BookmarkRequestDto request, Long memberId) {

        Member member = memberService.findById(memberId);
        Place place = placeService.findPlaceById(request.placeId());
        // 활성 상태의 북마크가 이미 있으면 중복
        bookmarkRepository.findByMemberAndPlaceAndDeletedAtIsNull(member, place)
                .ifPresent(b -> {
                    throw new BusinessException(ErrorCode.ALREADY_EXISTS_BOOKMARK);
                });
        // 소프트 삭제된 항목이 있었으면 재활성화
        var maybe = bookmarkRepository.findByMemberAndPlace(member, place); // Optional<Bookmark> 반환 받음
        if (maybe.isPresent()) {
            Bookmark exist = maybe.get();
            exist.reactivate();
            Bookmark saved = bookmarkRepository.save(exist);
            return BookmarkResponseDto.from(saved);
        }

        // 신규 생성
        Bookmark bookmark = Bookmark.create(member, place);
        Bookmark saved = bookmarkRepository.save(bookmark);
        return BookmarkResponseDto.from(saved);
    }

    /**
     * 북마크 목록 조회 (최근 저장 순), read-only 트랜잭션
     */
    @Transactional(readOnly = true)
    public List<BookmarkResponseDto> getList(Long memberId) {
        Member member = Member.builder().id(memberId).build();

        return bookmarkRepository.findAllByMemberAndDeletedAtIsNullOrderByCreatedAtDesc(member)
                .stream()
                .map(BookmarkResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 소프트 삭제: deletedAt = now()
     */
    @Transactional
    public void delete(Long memberId, Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_BOOKMARK));

        // 소유자 확인
        if (memberId == null || bookmark.getMember() == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN_BOOKMARK);
        }

        long ownerId = bookmark.getMember().getId();
        if (ownerId != memberId.longValue()) {
            throw new BusinessException(ErrorCode.FORBIDDEN_BOOKMARK);
        }

        if (bookmark.isDeleted()) {
            return; // 이미 삭제된 상태면 멱등성 보장
        }

        bookmark.delete(); // 엔티티 내 헬퍼 사용 (deletedAt = now())
        bookmarkRepository.save(bookmark);
    }
}
