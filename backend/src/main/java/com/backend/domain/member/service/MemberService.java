package com.backend.domain.member.service;

import com.backend.domain.member.dto.request.MemberSignupRequest;
import com.backend.domain.member.dto.request.MemberUpdateRequest;
import com.backend.domain.member.dto.response.MemberResponse;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.exception.BusinessException;
import com.backend.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse signup(MemberSignupRequest request) {
        validateDuplicate(request);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = request.toEntity(encodedPassword);
        memberRepository.save(member);

        return MemberResponse.from(member);
    }

    //TODO: 수정 시 비밀번호 입력하기

    @Transactional
    public MemberResponse updateMember(Long id, MemberUpdateRequest request) {
        Member member = findById(id);

        if(request.email() != null) member.updateEmail(request.email());
        if(request.nickname() != null) member.updateNickname(request.nickname());

        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse deleteMember(Long id) {
        Member member = findById(id);

        if (member.isDeleted()) {
            throw new BusinessException(ErrorCode.ALREADY_DELETED_MEMBER);
        }

        member.delete();
        return MemberResponse.from(member);
    }

    /** 회원 조회용 */
    @Transactional
    public MemberResponse getMember(Long id) {
        Member member = findById(id);
        return MemberResponse.from(member);
    }

    // TODO: Member, MemberResponse 각각 반환 메서드가 필요?

    @Transactional(readOnly = true)
    public Member findByMemberId(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Member findByIdEntity(Long memberPk) {
        return memberRepository.findById(memberPk)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateDuplicate(MemberSignupRequest request) {

        if (memberRepository.existsByMemberId(request.memberId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_MEMBER_ID);
        }

        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
