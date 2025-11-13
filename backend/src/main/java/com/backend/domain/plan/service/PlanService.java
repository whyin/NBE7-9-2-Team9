package com.backend.domain.plan.service;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.service.MemberService;
import com.backend.domain.plan.detail.repository.PlanDetailRepository;
import com.backend.domain.plan.dto.PlanCreateRequestBody;
import com.backend.domain.plan.dto.PlanResponseBody;
import com.backend.domain.plan.dto.PlanUpdateRequestBody;
import com.backend.domain.plan.entity.Plan;
import com.backend.domain.plan.entity.PlanMember;
import com.backend.domain.plan.repository.PlanMemberRepository;
import com.backend.domain.plan.repository.PlanRepository;
import com.backend.global.exception.BusinessException;
import com.backend.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final PlanMemberRepository planMemberRepository;
    private final MemberService memberService;
    private final PlanDetailRepository planDetailRepository;
    // TODO 회원 서비스 기반 처리 하기, JWT에서 멤버 ID 식별자 사용하면 더 편할것 같은데 보안상의 문제는 없는지?

    @Transactional
    public Plan createPlan(PlanCreateRequestBody planCreateRequestBody, long memberPkId) {
        Member member = Member.builder().id(memberPkId).build();
        Plan plan = planCreateRequestBody.toEntity(member);
        hasValidPlan(plan);
        Plan savedPlan = planRepository.save(plan);
        planMemberRepository.save(PlanMember.builder().member(member).plan(plan).build().inviteAccept()); // 단순 저장이므로 레포지토리 사용.
        return savedPlan;
    }


    public List<PlanResponseBody> getPlanList(long memberPkId) {
        List<Plan> plans = planRepository.getPlansByMember_Id(memberPkId);
        List<PlanResponseBody> planResponseBodies = plans.stream().map(PlanResponseBody::new).toList();
        return planResponseBodies;
    }

    @Transactional
    public PlanResponseBody updatePlan(long planId, PlanUpdateRequestBody planUpdateRequestBody, long memberPkId) {
        Member member = Member.builder().id(memberPkId).build();
        Plan plan = getPlanById(planId);
        isSameMember(plan, member);
        hasValidPlan(plan);

        plan.updatePlan(planUpdateRequestBody, member);
        planRepository.save(plan);
        return new PlanResponseBody(plan);
    }

    @Transactional
    public void deletePlanById(long planId, long memberPkId) {
        Plan plan = getPlanById(planId);
        Member member = Member.builder().id(memberPkId).build();
        isSameMember(plan, member);
        planMemberRepository.deletePlanMembersByPlan(plan);
        planDetailRepository.deletePlanDetailsByPlan(plan);
        planRepository.deleteById(planId);
    }

    public PlanResponseBody getPlanResponseBodyById(long planId) {
        return new PlanResponseBody(getPlanById(planId));
    }

    public Plan getPlanById(long planId) {
        return planRepository.findById(planId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_PLAN)
        );
    }

    public PlanResponseBody getTodayPlan(long memberPkId){
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        Plan plan = planRepository.getPlanByStartDateAndMemberId(todayStart, memberPkId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_PLAN)
        );
        return new PlanResponseBody(plan);
    }

    private void hasValidPlan(Plan plan) {
        if (plan.getStartDate().isAfter(plan.getEndDate())) {
            throw new BusinessException(ErrorCode.NOT_VALID_DATE);
        }
        if (plan.getStartDate().isBefore(LocalDateTime.now().toLocalDate().atStartOfDay().minusSeconds(1))) {
            throw new BusinessException(ErrorCode.NOT_VALID_DATE);
        }
        if (plan.getEndDate().isAfter(LocalDateTime.now().plusYears(10))) {
            throw new BusinessException(ErrorCode.NOT_VALID_DATE);
        }
    }

    private void isSameMember(Plan plan, Member member) {
        if (member.getId() != plan.getMember().getId()) {
            throw new BusinessException(ErrorCode.NOT_SAME_MEMBER);
        }
    }


}
