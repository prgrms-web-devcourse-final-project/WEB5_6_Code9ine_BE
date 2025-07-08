package com.grepp.spring.app.model.community_like.service;

import com.grepp.spring.app.model.community_like.domain.CommunityLike;
import com.grepp.spring.app.model.community_like.model.CommunityLikeDTO;
import com.grepp.spring.app.model.community_like.repos.CommunityLikeRepository;
import com.grepp.spring.app.model.community_post.domain.CommunityPost;
import com.grepp.spring.app.model.community_post.repos.CommunityPostRepository;
import com.grepp.spring.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CommunityLikeService {

    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityPostRepository communityPostRepository;

    public CommunityLikeService(final CommunityLikeRepository communityLikeRepository,
            final CommunityPostRepository communityPostRepository) {
        this.communityLikeRepository = communityLikeRepository;
        this.communityPostRepository = communityPostRepository;
    }

    public List<CommunityLikeDTO> findAll() {
        final List<CommunityLike> communityLikes = communityLikeRepository.findAll(Sort.by("likeId"));
        return communityLikes.stream()
                .map(communityLike -> mapToDTO(communityLike, new CommunityLikeDTO()))
                .toList();
    }

    public CommunityLikeDTO get(final Long likeId) {
        return communityLikeRepository.findById(likeId)
                .map(communityLike -> mapToDTO(communityLike, new CommunityLikeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CommunityLikeDTO communityLikeDTO) {
        final CommunityLike communityLike = new CommunityLike();
        mapToEntity(communityLikeDTO, communityLike);
        return communityLikeRepository.save(communityLike).getLikeId();
    }

    public void update(final Long likeId, final CommunityLikeDTO communityLikeDTO) {
        final CommunityLike communityLike = communityLikeRepository.findById(likeId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(communityLikeDTO, communityLike);
        communityLikeRepository.save(communityLike);
    }

    public void delete(final Long likeId) {
        communityLikeRepository.deleteById(likeId);
    }

    private CommunityLikeDTO mapToDTO(final CommunityLike communityLike,
            final CommunityLikeDTO communityLikeDTO) {
        communityLikeDTO.setLikeId(communityLike.getLikeId());
        communityLikeDTO.setMemberId(communityLike.getMemberId());
        communityLikeDTO.setCreatedAt(communityLike.getCreatedAt());
        communityLikeDTO.setActivated(communityLike.getActivated());
        communityLikeDTO.setCount(communityLike.getCount());
        communityLikeDTO.setPost(communityLike.getPost() == null ? null : communityLike.getPost().getPostId());
        return communityLikeDTO;
    }

    private CommunityLike mapToEntity(final CommunityLikeDTO communityLikeDTO,
            final CommunityLike communityLike) {
        communityLike.setMemberId(communityLikeDTO.getMemberId());
        communityLike.setCreatedAt(communityLikeDTO.getCreatedAt());
        communityLike.setActivated(communityLikeDTO.getActivated());
        communityLike.setCount(communityLikeDTO.getCount());
        final CommunityPost post = communityLikeDTO.getPost() == null ? null : communityPostRepository.findById(communityLikeDTO.getPost())
                .orElseThrow(() -> new NotFoundException("post not found"));
        communityLike.setPost(post);
        return communityLike;
    }

}
