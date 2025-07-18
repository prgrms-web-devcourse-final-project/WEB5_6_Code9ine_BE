package com.grepp.spring.app.model.community.repos;

import com.grepp.spring.app.model.community.domain.CommunityLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    // 커뮤니티 활성화된 좋아요 개수
    int countLikeByPost_PostIdAndActivatedTrue(Long postId);

    // 커뮤니티 좋아요 활성화 여부
    boolean existsByPost_PostIdAndMember_MemberId(Long postId, Long memberId);

    // 커뮤니티 현재 사용자의 활성화된 좋아요가 존재하는지 확인
    Optional<CommunityLike> findByPost_PostIdAndMember_MemberId(Long postId, Long memberId);
}
