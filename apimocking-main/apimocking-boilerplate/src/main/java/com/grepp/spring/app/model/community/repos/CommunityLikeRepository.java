package com.grepp.spring.app.model.community.repos;

import com.grepp.spring.app.model.community.domain.CommunityLike;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
    @Query("SELECT COUNT(c) FROM CommunityLike c WHERE c.post.postId = :postId")
    int countLikeByPostId(@Param("postId") Long postId);

    boolean existsByPost_PostIdAndMember_MemberId(Long postId, Long memberId);
}
