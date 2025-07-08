package com.grepp.spring.app.model.community_like.repos;

import com.grepp.spring.app.model.community_like.domain.CommunityLike;
import com.grepp.spring.app.model.community_post.domain.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    CommunityLike findFirstByPost(CommunityPost communityPost);

}
