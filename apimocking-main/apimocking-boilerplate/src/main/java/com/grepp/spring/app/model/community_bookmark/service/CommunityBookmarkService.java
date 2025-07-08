package com.grepp.spring.app.model.community_bookmark.service;

import com.grepp.spring.app.model.community_bookmark.domain.CommunityBookmark;
import com.grepp.spring.app.model.community_bookmark.model.CommunityBookmarkDTO;
import com.grepp.spring.app.model.community_bookmark.repos.CommunityBookmarkRepository;
import com.grepp.spring.app.model.community_post.domain.CommunityPost;
import com.grepp.spring.app.model.community_post.repos.CommunityPostRepository;
import com.grepp.spring.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CommunityBookmarkService {

    private final CommunityBookmarkRepository communityBookmarkRepository;
    private final CommunityPostRepository communityPostRepository;

    public CommunityBookmarkService(final CommunityBookmarkRepository communityBookmarkRepository,
            final CommunityPostRepository communityPostRepository) {
        this.communityBookmarkRepository = communityBookmarkRepository;
        this.communityPostRepository = communityPostRepository;
    }

    public List<CommunityBookmarkDTO> findAll() {
        final List<CommunityBookmark> communityBookmarks = communityBookmarkRepository.findAll(Sort.by("cBookmarkId"));
        return communityBookmarks.stream()
                .map(communityBookmark -> mapToDTO(communityBookmark, new CommunityBookmarkDTO()))
                .toList();
    }

    public CommunityBookmarkDTO get(final Long cBookmarkId) {
        return communityBookmarkRepository.findById(cBookmarkId)
                .map(communityBookmark -> mapToDTO(communityBookmark, new CommunityBookmarkDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CommunityBookmarkDTO communityBookmarkDTO) {
        final CommunityBookmark communityBookmark = new CommunityBookmark();
        mapToEntity(communityBookmarkDTO, communityBookmark);
        return communityBookmarkRepository.save(communityBookmark).getCBookmarkId();
    }

    public void update(final Long cBookmarkId, final CommunityBookmarkDTO communityBookmarkDTO) {
        final CommunityBookmark communityBookmark = communityBookmarkRepository.findById(cBookmarkId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(communityBookmarkDTO, communityBookmark);
        communityBookmarkRepository.save(communityBookmark);
    }

    public void delete(final Long cBookmarkId) {
        communityBookmarkRepository.deleteById(cBookmarkId);
    }

    private CommunityBookmarkDTO mapToDTO(final CommunityBookmark communityBookmark,
            final CommunityBookmarkDTO communityBookmarkDTO) {
        communityBookmarkDTO.setCBookmarkId(communityBookmark.getCBookmarkId());
        communityBookmarkDTO.setMemberId(communityBookmark.getMemberId());
        communityBookmarkDTO.setCreatedAt(communityBookmark.getCreatedAt());
        communityBookmarkDTO.setActivated(communityBookmark.getActivated());
        communityBookmarkDTO.setPost(communityBookmark.getPost() == null ? null : communityBookmark.getPost().getPostId());
        return communityBookmarkDTO;
    }

    private CommunityBookmark mapToEntity(final CommunityBookmarkDTO communityBookmarkDTO,
            final CommunityBookmark communityBookmark) {
        communityBookmark.setMemberId(communityBookmarkDTO.getMemberId());
        communityBookmark.setCreatedAt(communityBookmarkDTO.getCreatedAt());
        communityBookmark.setActivated(communityBookmarkDTO.getActivated());
        final CommunityPost post = communityBookmarkDTO.getPost() == null ? null : communityPostRepository.findById(communityBookmarkDTO.getPost())
                .orElseThrow(() -> new NotFoundException("post not found"));
        communityBookmark.setPost(post);
        return communityBookmark;
    }

}
