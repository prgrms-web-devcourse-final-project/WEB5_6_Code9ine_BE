package com.grepp.spring.app.model.post_image.repos;

import com.grepp.spring.app.model.community.domain.CommunityPost;
import com.grepp.spring.app.model.post_image.domain.PostImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findByPost(CommunityPost communityPost);

}
