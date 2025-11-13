package com.backend.domain.plan.detail.controller;

import com.backend.domain.auth.service.AuthService;
import com.backend.domain.plan.detail.dto.PlanDetailRequestBody;
import com.backend.domain.plan.detail.dto.PlanDetailResponseBody;
import com.backend.domain.plan.detail.dto.PlanDetailsElementBody;
import com.backend.domain.plan.detail.entity.PlanDetail;
import com.backend.domain.plan.detail.service.PlanDetailService;
import com.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan/detail")
@RequiredArgsConstructor
public class PlanDetailController {
    private final PlanDetailService planDetailService;
    private final AuthService authService;

    @PostMapping("/add")
    public ApiResponse<PlanDetailResponseBody> addPlanDetail(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @Valid @RequestBody PlanDetailRequestBody planDetailRequestBody
    ) {
        long memberPkId = authService.getMemberId(accessToken);
        PlanDetail planDetail = planDetailService.addPlanDetail(planDetailRequestBody, memberPkId);

        return ApiResponse.created(
                new PlanDetailResponseBody(planDetail)
        );
    }

    @GetMapping("/{planDetailId}")
    public ApiResponse<PlanDetailsElementBody> getPlanDetail(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @NotNull @PathVariable long planDetailId
    ) {
        long memberPkId = authService.getMemberId(accessToken);

        PlanDetailsElementBody planDetailsElementBody = planDetailService.getPlanDetailById(planDetailId, memberPkId);

        return ApiResponse.success(
                planDetailsElementBody
        );
    }

    @GetMapping("/{planId}/list")
    public ApiResponse<List<PlanDetailsElementBody>> getAllPlanDetail(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @NotNull @PathVariable long planId
    ) {
        long memberPkId = authService.getMemberId(accessToken);

        List<PlanDetailsElementBody> planDetailsElementBodies = planDetailService.getPlanDetailsByPlanId(planId, memberPkId);
        return ApiResponse.success(planDetailsElementBodies);
    }

    @GetMapping("/{planId}/todaylist")
    public ApiResponse<List<PlanDetailsElementBody>> getTodayPlanDetail(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @NotNull @PathVariable long planId
    ) {
        long memberPkId = authService.getMemberId(accessToken);
        List<PlanDetailsElementBody> planDetailsElementBodies = planDetailService.getTodayPlanDetails(planId,memberPkId);
        return ApiResponse.success(planDetailsElementBodies);
    }


    @PatchMapping("/update/{planDetailId}")
    public ApiResponse<PlanDetailResponseBody> updatePlanDetail(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @NotNull @PathVariable long planDetailId,
            @Valid @RequestBody PlanDetailRequestBody planDetailRequestBody
    ) {
        long memberPkId = authService.getMemberId(accessToken);

        PlanDetailResponseBody planDetailResponseBody = planDetailService.updatePlanDetail(planDetailRequestBody, memberPkId, planDetailId);
        return ApiResponse.success(
                planDetailResponseBody
        );
    }

    @DeleteMapping("/delete/{detailId}")
    public ApiResponse<Null> deletePlanDetail(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
            @NotNull @PathVariable long detailId
    ) {
        long memberPkId = authService.getMemberId(accessToken);
        planDetailService.deletePlanDetail(detailId, memberPkId);
        return ApiResponse.success();
    }
}
