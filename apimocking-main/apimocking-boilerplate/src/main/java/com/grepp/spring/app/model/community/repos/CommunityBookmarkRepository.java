package com.grepp.spring.app.model.community.repos;

import com.grepp.spring.app.model.community.domain.CommunityBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityBookmarkRepository extends JpaRepository<CommunityBookmark, Long> {
    boolean existsByPost_PostIdAndMember_MemberId(Long postId, Long memberId);
}
