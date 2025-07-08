package com.grepp.spring.app.model.community_post.repos;

import com.grepp.spring.app.model.community_post.domain.CommunityPost;
import com.grepp.spring.app.model.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    CommunityPost findFirstByMember(Member member);

}
