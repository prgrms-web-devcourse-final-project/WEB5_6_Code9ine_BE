package com.grepp.spring.app.model.admin.service;

import com.grepp.spring.app.model.admin.dto.AdminUserListResponse;
import com.grepp.spring.app.model.admin.dto.AdminUserResponse;
import com.grepp.spring.infra.payload.PageParam;
import java.util.List;

public interface AdminUserService {

    // 관리자 모든 유저 조회
    AdminUserListResponse getAllUsers();

    // 관리자 유저 닉네임으로 검색
    AdminUserResponse getUserByNickname(String nickname);

    // 관리자 유저 차단
    void blockUser(Long memberId);

}
