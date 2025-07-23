package com.grepp.spring.app.model.community.repos;

import com.grepp.spring.app.model.community.domain.CommunityComment;
import feign.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    // 커뮤니티 게시글 댓글 수
    int countByPost_PostIdAndActivatedTrue(Long postId);

    // 커뮤니티 게시글 댓글 조회
    List<CommunityComment> findByPost_PostIdAndActivatedTrue(Long postId);

    // 커뮤니티 게시글 댓글 삭제
    Optional<CommunityComment> findByCommentIdAndActivatedTrue(Long commentId);

    // 로그인 한 사용자 커뮤니티 댓글 수 ( credated이 이번달이여야함)
    @Query("""
            SELECT COUNT(c) FROM CommunityComment c
            WHERE c.member.memberId = :memberId
            AND c.activated = true AND c.createdAt BETWEEN :start AND :end
            """)
    int countAchievedComment(@Param("memberId") Long memberId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end);
}
