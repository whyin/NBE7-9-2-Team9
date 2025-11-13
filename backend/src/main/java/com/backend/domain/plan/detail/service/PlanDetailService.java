package com.backend.domain.plan.detail.service;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.service.MemberService;
import com.backend.domain.place.entity.Place;
import com.backend.domain.place.service.PlaceService;
import com.backend.domain.plan.detail.dto.PlanDetailRequestBody;
import com.backend.domain.plan.detail.dto.PlanDetailResponseBody;
import com.backend.domain.plan.detail.dto.PlanDetailsElementBody;
import com.backend.domain.plan.detail.entity.PlanDetail;
import com.backend.domain.plan.detail.repository.PlanDetailRepository;
import com.backend.domain.plan.entity.Plan;
import com.backend.domain.plan.service.PlanMemberService;
import com.backend.domain.plan.service.PlanService;
import com.backend.global.exception.BusinessException;
import com.backend.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanDetailService {
    private final PlanService planService;
    private final PlanMemberService planMemberService;
    private final MemberService memberService;
    private final PlaceService placeService;

    private final PlanDetailRepository planDetailRepository;

    @Transactional
    public PlanDetail addPlanDetail(PlanDetailRequestBody requestBody, long memberPkId) {
        Member member = getAvailableMember(requestBody.planId(),memberPkId);
        Plan plan = planService.getPlanById(requestBody.planId());
        Place place = placeService.findPlaceById(requestBody.placeId());

        PlanDetail planDetail = new PlanDetail(member, plan, place, requestBody);
        checkValidTime(requestBody, plan,planDetail);
        PlanDetail savedPlanDetail = this.planDetailRepository.save(planDetail);
        return savedPlanDetail;
    }


    public PlanDetailsElementBody getPlanDetailById(long planDetailId, long memberPkId) {
        PlanDetail planDetail = getPlanDetailById(planDetailId);
        getAvailableMember(planDetail.getPlan().getId(), memberPkId);

        return new PlanDetailsElementBody(planDetail);
    }

    @Transactional
    public List<PlanDetailsElementBody> getPlanDetailsByPlanId(long planId, long memberPkId) {
        getAvailableMember(planId, memberPkId);

        List<PlanDetail> planDetails = planDetailRepository.getPlanDetailsByPlanId(planId);

        List<PlanDetailsElementBody> planDetailList = planDetails.stream().map(PlanDetailsElementBody::new).toList();

        return planDetailList;
    }


    public List<PlanDetailsElementBody> getTodayPlanDetails(long planId ,long memberPkId) {
        getAvailableMember(planId, memberPkId);
        List<PlanDetail> planDetails = planDetailRepository.getPlanDetailsByPlanId(planId);
        return planDetails.stream().filter(planDetail ->
                planDetail.getEndTime().isAfter(LocalDateTime.now().toLocalDate().atStartOfDay()) && planDetail.getStartTime().isBefore(LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX)
            )
        ).map(PlanDetailsElementBody::new).toList();
    }

    @Transactional
    public PlanDetailResponseBody updatePlanDetail(PlanDetailRequestBody planDetailRequestBody, long memberPkId, long planDetailId) {
        getAvailableMember(planDetailRequestBody.planId(), memberPkId);

        Place place = placeService.findPlaceById(planDetailRequestBody.placeId());
        PlanDetail planDetail = getPlanDetailById(planDetailId);
        checkValidTime(planDetailRequestBody, planService.getPlanById(planDetailRequestBody.planId()), planDetail);
        planDetail.updatePlanDetail(planDetailRequestBody, place);
        planDetailRepository.save(planDetail);
        return new PlanDetailResponseBody(planDetail);
    }

    @Transactional
    public void deletePlanDetail(long planDetailId, long memberPkId) {
        PlanDetail planDetail = getPlanDetailById(planDetailId);
        getAvailableMember(planDetail.getPlan().getId(), memberPkId);
        planDetailRepository.deleteById(planDetailId);
    }



    private PlanDetail getPlanDetailById(long planDetailId) {
        return planDetailRepository.getPlanDetailById(planDetailId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_DETAIL_PLAN));
    }


    private Member getAvailableMember(long planId, long memberPkId) {
        Member member = memberService.findById(memberPkId);
        Plan plan = planService.getPlanById(planId);
        if (!planMemberService.isAvailablePlanMember(planId, member)) {
            throw new BusinessException(ErrorCode.NOT_ALLOWED_MEMBER);
        }
        return member;
    }

    //시간이 유효한 시간인지
    private void checkValidTime(PlanDetailRequestBody planDetailRequestBody, Plan plan,PlanDetail planDetail) {
        // 계획 내에서 시간이 겹치지 않는지 검사
        if (planDetailRepository.existsOverlapping(planDetailRequestBody.planId(), planDetailRequestBody.startTime(), planDetailRequestBody.endTime(), planDetail.getId())) {
            throw new BusinessException(ErrorCode.CONFLICT_TIME);
        }

        //계획 안의 시간인지
        if(planDetailRequestBody.startTime().isBefore(plan.getStartDate()) || planDetailRequestBody.endTime().isAfter(plan.getEndDate())) {
            throw new BusinessException(ErrorCode.NOT_VALID_DATE);
        }
        // 지금으로부터 10년 뒤 까지만 계획 설정 가능
        if (planDetailRequestBody.startTime().isAfter(LocalDateTime.now().plusYears(10))) {
            throw new BusinessException(ErrorCode.NOT_VALID_DATE);
        }
    }


}
