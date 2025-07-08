package com.grepp.spring.app.model.community_comment.repos;

import com.grepp.spring.app.model.community_comment.domain.CommunityComment;
import com.grepp.spring.app.model.community_post.domain.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    CommunityComment findFirstByPost(CommunityPost communityPost);

}
