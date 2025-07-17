package com.grepp.spring.app.model.community.repos;

import com.grepp.spring.app.model.challenge.code.CommunityCategory;
import com.grepp.spring.app.model.community.domain.CommunityPost;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<CommunityPost, Long> {

    Page<CommunityPost> findByCategoryAndActivatedIsTrue(CommunityCategory category, Pageable pageable);

    Optional<CommunityPost> findByPostIdAndActivatedIsTrue(Long postId);
}
