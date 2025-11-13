package com.backend.domain.admin.controller;

import com.backend.domain.admin.service.AdminMemberService;
import com.backend.domain.admin.dto.response.MemberAdminResponse;
import com.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    /** 전체 회원 조회 */
    @GetMapping
    public ApiResponse<List<MemberAdminResponse>> getAllMembers() {
        return ApiResponse.success(adminMemberService.getAllMembers());
    }

    /** 회원 단건 조회 */
    @GetMapping("/{id}")
    public ApiResponse<MemberAdminResponse> getMemberById(@PathVariable Long id) {
        return ApiResponse.success(adminMemberService.getMemberById(id));
    }

    /** 회원 삭제 */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMember(@PathVariable Long id) {
        adminMemberService.deleteMember(id);
        return ApiResponse.success(null, "회원 삭제 완료");
    }

    /** 회원 RefreshToken 무효화 */
    @DeleteMapping("/{id}/token")
    public ApiResponse<Void> invalidateRefreshToken(@PathVariable Long id) {
        adminMemberService.invalidateRefreshToken(id);
        return ApiResponse.success(null, "RefreshToken 무효화 완료");
    }
}
