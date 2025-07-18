package com.grepp.spring.app.model.admin.service;

import com.grepp.spring.app.model.admin.dto.AdminUserResponse;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.store.repos.StoreRepository;
import com.grepp.spring.infra.payload.PageParam;
import com.grepp.spring.util.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final MemberRepository memberRepository;

    // 관리자 모든 유저 조회
    @Transactional(readOnly = true)
    @Override
    public List<AdminUserResponse> getAllUsers(PageParam pageParam) {
        Pageable pageable = pageParam.toPageable(Sort.by(Sort.Direction.ASC,"memberId"));

        return memberRepository.findByRoleEquals("ROLE_USER", pageable)
            .map(member -> new AdminUserResponse(
                member.getMemberId(),
                member.getNickname(),
                member.getEmail(),
                member.getActivated()
            ))
            .toList();
    }

    // 관리자 유저 닉네임으로 검색
    @Transactional(readOnly = true)
    @Override
    public AdminUserResponse getUserByNickname(String nickname) {
        Member member = memberRepository.findByNicknameAndRole(nickname, "ROLE_USER")
            .orElseThrow(() -> new NotFoundException("해당 닉네임의 유저를 찾을 수 없습니다."));

        return new AdminUserResponse(
            member.getMemberId(),
            member.getNickname(),
            member.getEmail(),
            member.getActivated()
        );
    }

    // 관리자 유저 차단
    @Transactional
    @Override
    public void blockUser(Long memberId) {
        Member member = memberRepository.findByMemberIdAndRole(memberId, "ROLE_USER")
            .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));

        member.unActivated();
    }

}
