package com.grepp.spring.app.model.admin.service;

import com.grepp.spring.app.model.admin.dto.AdminUserResponse;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.infra.payload.PageParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

public interface AdminService {

    // 관리자 모든 유저 조회
    List<AdminUserResponse> getAllUsers(PageParam pageParam);

    // 관리자 유저 닉네임으로 검색
    AdminUserResponse getUserByNickname(String nickname);
}
