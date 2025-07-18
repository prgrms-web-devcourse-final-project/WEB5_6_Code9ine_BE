package com.grepp.spring.app.model.community.repos;

import com.grepp.spring.app.model.community.domain.CommunityBookmark;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityBookmarkRepository extends JpaRepository<CommunityBookmark, Long> {

    // 커뮤니티 현재 사용자의 활성화된 북마크가 존재하는지 확인
    Optional<CommunityBookmark> findByPost_PostIdAndMember_MemberId(Long postId, Long memberId);

    // 커뮤니티 북마크 활성화 여부
    boolean existsByPost_PostIdAndMember_MemberId(Long postId, Long memberId);
}
