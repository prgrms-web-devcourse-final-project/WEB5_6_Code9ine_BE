package com.grepp.spring.app.model.community.repos;

import com.grepp.spring.app.model.community.domain.CommunityComment;
import com.grepp.spring.app.model.community.domain.CommunityLike;
import feign.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    // 커뮤니티 게시글 댓글 수
    @Query("SELECT COUNT(c) FROM CommunityComment c WHERE c.post.postId = :postId")
    int countCommentByPostId(@Param("postId") Long postId);

    // 커뮤니티 게시글 댓글 조회
    List<CommunityComment> findByPost_PostIdAndActivatedTrue(Long postId);

    // 커뮤니티 게시글 댓글 삭제
    Optional<CommunityComment> findByCommentIdAndActivatedTrue(Long commentId);
}
