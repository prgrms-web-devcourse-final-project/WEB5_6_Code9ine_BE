package com.grepp.spring.app.model.post_image.repos;

import com.grepp.spring.app.model.community.domain.CommunityPost;
import com.grepp.spring.app.model.post_image.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    PostImage findFirstByPost(CommunityPost communityPost);

}
