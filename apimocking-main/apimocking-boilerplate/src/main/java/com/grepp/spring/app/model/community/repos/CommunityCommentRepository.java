package com.grepp.spring.app.model.community.repos;

import com.grepp.spring.app.model.community.domain.CommunityComment;
import com.grepp.spring.app.model.community.domain.CommunityLike;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    @Query("SELECT COUNT(c) FROM CommunityComment c WHERE c.post.postId = :postId")
    int countCommentByPostId(@Param("postId") Long postId);

}
