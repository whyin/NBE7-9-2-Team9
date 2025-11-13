package com.backend.domain.plan.service;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.service.MemberService;
import com.backend.domain.plan.dto.PlanMemberMyResponseBody;
import com.backend.domain.plan.dto.PlanMemberAddRequestBody;
import com.backend.domain.plan.dto.PlanMemberAnswerRequestBody;
import com.backend.domain.plan.dto.PlanMemberResponseBody;
import com.backend.domain.plan.entity.Plan;
import com.backend.domain.plan.entity.PlanMember;
import com.backend.domain.plan.repository.PlanMemberRepository;
import com.backend.global.exception.BusinessException;
import com.backend.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanMemberService {
    private final PlanMemberRepository planMemberRepository;
    private final PlanService planService;
    private final MemberService memberService;

    public PlanMemberResponseBody invitePlanMember(PlanMemberAddRequestBody requestBody, long memberId) {
        PlanMember planMember = isValidInvite(requestBody, memberId);
        planMemberRepository.save(planMember);
        return new PlanMemberResponseBody(planMember);
    }

    public List<PlanMemberMyResponseBody> myInvitedPlanList(long memberPkId) {
        Member member = Member.builder().id(memberPkId).build();

        List<PlanMember> planMemberList = planMemberRepository.getPlanMembersByMember(member);
        List<PlanMemberMyResponseBody> myPlanMemberList =
                planMemberList
                        .stream()
                        .map(pm -> new PlanMemberMyResponseBody(pm))
                        .toList();
        return myPlanMemberList;
    }


    public PlanMemberResponseBody DeletePlanMember(PlanMemberAddRequestBody requestBody, long memberPkId) {
        PlanMember planMember = isValidInvite(requestBody, memberPkId);

        planMemberRepository.delete(planMember);
        return new PlanMemberResponseBody(planMember);
    }

    public PlanMemberResponseBody acceptInvitePlanMember(PlanMemberAnswerRequestBody requestBody, long memberPkId) {
        PlanMember planMember = isMyInvite(requestBody, memberPkId);
        planMember.inviteAccept();
        planMemberRepository.save(planMember);
        return new PlanMemberResponseBody(planMember);
    }

    public PlanMemberResponseBody denyInvitePlanMember(PlanMemberAnswerRequestBody requestBody, long memberPkId) {
        PlanMember planMember = isMyInvite(requestBody, memberPkId);
        planMember.inviteDeny();
        planMemberRepository.save(planMember);
        return new PlanMemberResponseBody(planMember);
    }

    private PlanMember isValidInvite(PlanMemberAddRequestBody requestBody, long memberId) {
        Plan plan = planService.getPlanById(requestBody.planId());
        if (plan.getMember().getId() != memberId) {
            throw new BusinessException(ErrorCode.NOT_MY_PLAN);
        }

        Member invitedMember = memberService.findById(requestBody.memberId());

        // 데이터 베이스 오류 처리를 서비스 로직 처리로 변경
        if(planMemberRepository.existsByMemberInPlanId(invitedMember.getId(),plan.getId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_MEMBER_INVITE);
        };

        PlanMember planMember = PlanMember.builder().member(invitedMember).plan(plan).build();
        return planMember;
    }

    private PlanMember isMyInvite(PlanMemberAnswerRequestBody requestBody, long memberPkId) {
        Member member = Member.builder().id(memberPkId).build();

        Plan plan = planService.getPlanById(requestBody.planId());

        if (requestBody.memberId() != member.getId()) {
            throw new BusinessException(ErrorCode.NOT_MY_PLAN);
        }

        PlanMember planMember = planMemberRepository.findById(requestBody.planMemberId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_INVITE)
        );

        return planMember;
    }

    public boolean isAvailablePlanMember(long planId, Member member) {
        return planMemberRepository.existsByMemberInPlanId(member.getId(),planId);
    }
}
