package com.backend.domain.plan.controller;

import com.backend.domain.auth.service.AuthService;
import com.backend.domain.plan.dto.*;
import com.backend.domain.plan.entity.Plan;
import com.backend.domain.plan.service.PlanMemberService;
import com.backend.domain.plan.service.PlanService;
import com.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plan")
@Tag(name = "여행 계획 Api", description = "여행 계획을 생성하고 관리합니다.")
public class PlanController {
    private final PlanService planService;
    private final PlanMemberService planMemberService;
    private final AuthService authService;

    @PostMapping("/create")
    @Operation(summary = "여행 계획을 생성합니다.", description = "여행 계획을 생성합니다.")
    public ApiResponse<PlanResponseBody> create(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @Valid @RequestBody PlanCreateRequestBody planCreateRequestBody
    ) {
        long memberPkId = authService.getMemberId(accessToken);

        Plan plan = planService.createPlan(planCreateRequestBody, memberPkId);
        PlanResponseBody planResponseBody = new PlanResponseBody(plan);
        return ApiResponse.created(planResponseBody);
    }

    @GetMapping("/list")
    public ApiResponse<List<PlanResponseBody>> getList(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken
    ) {
        long memberPkId = authService.getMemberId(accessToken);
        List<PlanResponseBody> plans = planService.getPlanList(memberPkId);
        return ApiResponse.success(plans);
    }

    @GetMapping("/todayPlan")
    public ApiResponse<PlanResponseBody> getTodayPlan(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken
    ) {
        long memberPkId = authService.getMemberId(accessToken);
        return ApiResponse.success(planService.getTodayPlan(memberPkId));
    }

    @PatchMapping("/update/{planId}")
    public ApiResponse<PlanResponseBody> updatePlan(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @Valid @RequestBody PlanUpdateRequestBody planUpdateRequestBody,
            @PathVariable long planId
    ) {
        //TODO JWT 토큰에서 멤버 아이디 정보 가져오기
        long memberPkId = authService.getMemberId(accessToken);

        PlanResponseBody planResponseBody = planService.updatePlan(planId, planUpdateRequestBody, memberPkId);

        return ApiResponse.success(planResponseBody);
    }

    @GetMapping("/{planId}")
    public ApiResponse<PlanResponseBody> getPlan(
            @NotNull @PathVariable long planId
    ) {
        PlanResponseBody planResponseBody = planService.getPlanResponseBodyById(planId);
        return ApiResponse.success(planResponseBody);
    }

    @DeleteMapping("/delete/{planId}")
    public ResponseEntity deletePlan(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @NotNull @PathVariable long planId
    ) {
        long memberPkId = authService.getMemberId(accessToken);

        planService.deletePlanById(planId, memberPkId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/member/invite")
    public ApiResponse<PlanMemberResponseBody> inviteMember(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @Valid @RequestBody PlanMemberAddRequestBody memberRequestBody
    ) {
        long memberPkId = authService.getMemberId(accessToken);
        PlanMemberResponseBody planMemberResponseBody = planMemberService.invitePlanMember(memberRequestBody, memberPkId);
        return ApiResponse.success(planMemberResponseBody);
    }

    @GetMapping("/member/mylist")
    public ApiResponse<List<PlanMemberMyResponseBody>> getMyPlanMember(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken
            ) {
        long memberPkId = authService.getMemberId(accessToken);
        return ApiResponse.success(planMemberService.myInvitedPlanList(memberPkId));
    }

    @PatchMapping("/member/accept")
    public ApiResponse<PlanMemberResponseBody> acceptMember(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @Valid @RequestBody PlanMemberAnswerRequestBody memberAnswerRequestBody
    ) {
        long memberPkId = authService.getMemberId(accessToken);
        PlanMemberResponseBody planMemberResponseBody = planMemberService.acceptInvitePlanMember(memberAnswerRequestBody, memberPkId);

        return ApiResponse.success(planMemberResponseBody);
    }

    @PatchMapping("/member/deny")
    public ApiResponse<PlanMemberResponseBody> denyMember(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @Valid @RequestBody PlanMemberAnswerRequestBody memberAnswerRequestBody
    ) {
        long memberPkId = authService.getMemberId(accessToken);
        PlanMemberResponseBody planMemberResponseBody = planMemberService.denyInvitePlanMember(memberAnswerRequestBody, memberPkId);

        return ApiResponse.success(planMemberResponseBody);
    }
}
