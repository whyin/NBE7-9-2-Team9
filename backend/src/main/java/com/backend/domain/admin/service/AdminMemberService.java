package com.backend.domain.admin.service;

import com.backend.domain.admin.dto.response.MemberAdminResponse;
import com.backend.domain.auth.repository.RefreshTokenRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.entity.Role;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.exception.BusinessException;
import com.backend.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public List<MemberAdminResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberAdminResponse::from)
                .toList();
    }

    public MemberAdminResponse getMemberById(Long id) {
        Member member = getMember(id);
        return MemberAdminResponse.from(member);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = getMember(id);

        // 관리자 삭제 방지
        if (member.getRole() == Role.ADMIN) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 관련 RefreshToken 삭제
        invalidateRefreshToken(member.getId());

        memberRepository.deleteById(id);
    }

    @Transactional
    public void invalidateRefreshToken(Long id) {
        refreshTokenRepository.deleteByMemberPk(id);
    }

    /** 공통 메서드 */

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
