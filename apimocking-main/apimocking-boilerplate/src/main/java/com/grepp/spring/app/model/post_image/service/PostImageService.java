package com.grepp.spring.app.model.post_image.service;

import com.grepp.spring.app.model.community.domain.CommunityPost;
import java.util.List;

public interface PostImageService {

    void updatePostImages(CommunityPost post, List<String> updatedImageUrls);

}
