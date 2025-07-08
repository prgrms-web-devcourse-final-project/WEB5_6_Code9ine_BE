package com.grepp.spring.app.model.post_image.service;

import com.grepp.spring.app.model.community_post.domain.CommunityPost;
import com.grepp.spring.app.model.community_post.repos.CommunityPostRepository;
import com.grepp.spring.app.model.post_image.domain.PostImage;
import com.grepp.spring.app.model.post_image.model.PostImageDTO;
import com.grepp.spring.app.model.post_image.repos.PostImageRepository;
import com.grepp.spring.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final CommunityPostRepository communityPostRepository;

    public PostImageService(final PostImageRepository postImageRepository,
            final CommunityPostRepository communityPostRepository) {
        this.postImageRepository = postImageRepository;
        this.communityPostRepository = communityPostRepository;
    }

    public List<PostImageDTO> findAll() {
        final List<PostImage> postImages = postImageRepository.findAll(Sort.by("imageId"));
        return postImages.stream()
                .map(postImage -> mapToDTO(postImage, new PostImageDTO()))
                .toList();
    }

    public PostImageDTO get(final Long imageId) {
        return postImageRepository.findById(imageId)
                .map(postImage -> mapToDTO(postImage, new PostImageDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PostImageDTO postImageDTO) {
        final PostImage postImage = new PostImage();
        mapToEntity(postImageDTO, postImage);
        return postImageRepository.save(postImage).getImageId();
    }

    public void update(final Long imageId, final PostImageDTO postImageDTO) {
        final PostImage postImage = postImageRepository.findById(imageId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(postImageDTO, postImage);
        postImageRepository.save(postImage);
    }

    public void delete(final Long imageId) {
        postImageRepository.deleteById(imageId);
    }

    private PostImageDTO mapToDTO(final PostImage postImage, final PostImageDTO postImageDTO) {
        postImageDTO.setImageId(postImage.getImageId());
        postImageDTO.setImageUrl(postImage.getImageUrl());
        postImageDTO.setCreatedAt(postImage.getCreatedAt());
        postImageDTO.setSortOrder(postImage.getSortOrder());
        postImageDTO.setPost(postImage.getPost() == null ? null : postImage.getPost().getPostId());
        return postImageDTO;
    }

    private PostImage mapToEntity(final PostImageDTO postImageDTO, final PostImage postImage) {
        postImage.setImageUrl(postImageDTO.getImageUrl());
        postImage.setCreatedAt(postImageDTO.getCreatedAt());
        postImage.setSortOrder(postImageDTO.getSortOrder());
        final CommunityPost post = postImageDTO.getPost() == null ? null : communityPostRepository.findById(postImageDTO.getPost())
                .orElseThrow(() -> new NotFoundException("post not found"));
        postImage.setPost(post);
        return postImage;
    }

}
