package com.grepp.spring.app.model.community.repos;

import com.grepp.spring.app.model.challenge.code.CommunityCategory;
import com.grepp.spring.app.model.community.domain.CommunityPost;
import com.grepp.spring.app.model.member.domain.Member;
import feign.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface CommunityRepository extends JpaRepository<CommunityPost, Long> {

    // 커뮤니티 게시글 카테고리별 조회
    Page<CommunityPost> findByCategoryAndActivatedIsTrueAndMember_ActivatedTrue(CommunityCategory category, Pageable pageable);

    // 커뮤니티 게시글 존재 여부 판별
    Optional<CommunityPost> findByPostIdAndActivatedIsTrueAndMember_ActivatedTrue(Long postId);

    // 커뮤니티 게시글 단건 조회 + 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM CommunityPost p WHERE p.postId = :postId AND p.activated = true AND p.member.activated = true")
    Optional<CommunityPost> findActivatedPostWithLock(@Param("postId") Long postId);

    // 커뮤니티 인기 게시글 조회
    List<CommunityPost> findTop10ByActivatedIsTrueAndMember_ActivatedTrueOrderByLikeCountDesc();

    // 내가 작성한 게시글 목록 조회 (최신순 정렬)
    List<CommunityPost> findByMember_MemberIdAndActivatedIsTrueAndMember_ActivatedTrueOrderByCreatedAtDesc(Long memberId);

    int countByMemberAndCategoryAndActivatedIsTrueAndMember_ActivatedTrue(Member member, CommunityCategory category);
}
