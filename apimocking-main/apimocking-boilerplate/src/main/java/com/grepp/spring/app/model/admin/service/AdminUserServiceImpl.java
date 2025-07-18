package com.grepp.spring.app.model.admin.service;

import com.grepp.spring.app.model.admin.dto.AdminUserResponse;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.infra.payload.PageParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    @Override
    public List<AdminUserResponse> getAllUsers(PageParam pageParam) {
        Pageable pageable = pageParam.toPageable(Sort.by(Sort.Direction.ASC,"memberId"));

        return memberRepository.findByRoleEquals("ROLE_USER", pageable)
            .map(member -> new AdminUserResponse(
                member.getMemberId().intValue(),
                member.getNickname(),
                member.getEmail(),
                member.getActivated()
            ))
            .toList();
    }
}
