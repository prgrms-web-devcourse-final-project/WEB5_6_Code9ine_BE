package com.grepp.spring.app.model.community_bookmark.repos;

import com.grepp.spring.app.model.community_bookmark.domain.CommunityBookmark;
import com.grepp.spring.app.model.community_post.domain.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommunityBookmarkRepository extends JpaRepository<CommunityBookmark, Long> {

    CommunityBookmark findFirstByPost(CommunityPost communityPost);

}
