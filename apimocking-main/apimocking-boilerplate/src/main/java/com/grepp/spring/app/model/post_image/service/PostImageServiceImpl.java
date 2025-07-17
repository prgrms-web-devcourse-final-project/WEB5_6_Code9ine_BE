package com.grepp.spring.app.model.post_image.service;

import com.grepp.spring.app.model.community.domain.CommunityPost;
import com.grepp.spring.app.model.post_image.domain.PostImage;
import com.grepp.spring.app.model.post_image.repos.PostImageRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PostImageServiceImpl implements PostImageService{

    private final PostImageRepository postImageRepository;

    @Transactional
    @Override
    public void updatePostImages(CommunityPost post, List<String> updatedImageUrls) {
        Set<PostImage> currentImages = post.getImages();

        // 기존 이미지와 새로운 이미지 요청 비교
        List<String> existingUrls = currentImages.stream()
            .map(PostImage::getImageUrl)
            .toList();

        // 삭제한 이미지
        List<PostImage> toRemove = currentImages.stream()
            .filter(img -> !updatedImageUrls.contains(img.getImageUrl()))
            .toList();

        // 새로운 이미지
        List<PostImage> toAdd = new ArrayList<>();
        for (int i = 0; i < updatedImageUrls.size(); i++) {
            String url = updatedImageUrls.get(i);
            if (!existingUrls.contains(url)) {
                toAdd.add(PostImage.builder()
                    .post(post)
                    .imageUrl(url)
                    .sortOrder(i)
                    .build());
            }
        }

        // 삭제
        if (!toRemove.isEmpty()) {
            postImageRepository.deleteAll(toRemove);
        }

        // 추가
        if (!toAdd.isEmpty()) {
            postImageRepository.saveAll(toAdd);
        }
    }
}
