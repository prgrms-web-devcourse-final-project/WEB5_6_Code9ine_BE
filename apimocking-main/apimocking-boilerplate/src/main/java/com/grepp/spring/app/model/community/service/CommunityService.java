package com.grepp.spring.app.model.community.service;

import com.grepp.spring.app.model.community.dto.CommunityCommentCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityCommentResponse;
import com.grepp.spring.app.model.community.dto.CommunityPostCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityPostDetailResponse;
import com.grepp.spring.app.model.community.dto.CommunityPostUpdateRequest;
import com.grepp.spring.app.model.community.dto.CommunityTopPostResponse;
import com.grepp.spring.app.model.community.dto.CommunityUserInfoResponse;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.infra.payload.PageParam;
import java.util.List;
import java.util.Map;

public interface CommunityService {

    // 커뮤니티 로그인 유저 정보
    CommunityUserInfoResponse getMyInfo(Long memberId);

    // 커뮤니티 게시글 생성
    void createPost(CommunityPostCreateRequest request, Member member);

    // 커뮤니티 게시글 카테고리별 조회
    List<CommunityPostDetailResponse> getPostsByCategory(String category, PageParam pageParam, Long memberId);

    // 커뮤니티 게시글 수정
    void updatePost(Long postId, CommunityPostUpdateRequest request, Long memberId);

    // 커뮤니티 게시글 삭제(soft delete)
    void deletePost(Long postId, Long memberId);

    // 커뮤니티 게시글 단건 조회
    CommunityPostDetailResponse getPostById(Long postId, Long memberId);

    // 커뮤니티 게시글 댓글 조회
    List<CommunityCommentResponse> getCommentsByPostId(Long postId);

    // 커뮤니티 게시글 댓글 작성
    void createComment(Long postId, CommunityCommentCreateRequest request, Member member);

    // 커뮤니티 게시글 댓글 삭제
    void deleteComment(Long commentId, Long memberId);

    // 커뮤니티 게시글 좋아요 활성화/비활성화
    boolean toggleLike(Long postId, Long memberId);

    // 커뮤니티 게시글 북마크 활성화/비활성화
    boolean toggleBookmark(Long postId, Long memberId);

    // 커뮤니티 인기 게시글 조회
    List<CommunityTopPostResponse> getTopPosts();

    // 내가 작성한 게시글 목록 조회 (마이페이지용)
    List<Map<String, Object>> getMyPosts(Long memberId);

    // 내가 북마크한 게시글 목록 조회 (마이페이지용)
    List<Map<String, Object>> getBookmarkedPosts(Long memberId);
    
    // 특정 사용자가 특정 게시글을 북마크했는지 확인
    boolean isPostBookmarkedByUser(Long postId, Long memberId);
    
    // 특정 사용자가 특정 게시글을 좋아요했는지 확인
    boolean isPostLikedByUser(Long postId, Long memberId);
}
