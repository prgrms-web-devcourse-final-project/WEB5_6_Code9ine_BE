package com.grepp.spring.app.model.community.service;

import com.grepp.spring.app.model.community.dto.CommunityPostCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityPostDetailResponse;
import com.grepp.spring.app.model.community.dto.CommunityPostUpdateRequest;
import com.grepp.spring.app.model.community.dto.CommunityUserInfoResponse;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.infra.payload.PageParam;
import java.util.List;

public interface CommunityService {

    // 커뮤니티 로그인 유저 정보
    CommunityUserInfoResponse getMyInfo(Long memberId);

    // 커뮤니티 게시글 생성
    void createPost(CommunityPostCreateRequest request, Member member);

    // 커뮤니티 게시글 카테고리별 조회
    List<CommunityPostDetailResponse> getPostsByCategory(String category, PageParam pageParam, Long userId);

    // 커뮤니티 게시글 수정
    void updatePost(Long postId, CommunityPostUpdateRequest request, Long memberId);

    // 커뮤니티 게시글 삭제(soft delete)
    void deletePost(Long postId, Long memberId);
}
