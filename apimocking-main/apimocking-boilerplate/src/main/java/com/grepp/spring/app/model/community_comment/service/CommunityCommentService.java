package com.grepp.spring.app.model.community_comment.service;

import com.grepp.spring.app.model.community_comment.domain.CommunityComment;
import com.grepp.spring.app.model.community_comment.model.CommunityCommentDTO;
import com.grepp.spring.app.model.community_comment.repos.CommunityCommentRepository;
import com.grepp.spring.app.model.community_post.domain.CommunityPost;
import com.grepp.spring.app.model.community_post.repos.CommunityPostRepository;
import com.grepp.spring.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CommunityCommentService {

    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityPostRepository communityPostRepository;

    public CommunityCommentService(final CommunityCommentRepository communityCommentRepository,
            final CommunityPostRepository communityPostRepository) {
        this.communityCommentRepository = communityCommentRepository;
        this.communityPostRepository = communityPostRepository;
    }

    public List<CommunityCommentDTO> findAll() {
        final List<CommunityComment> communityComments = communityCommentRepository.findAll(Sort.by("commentId"));
        return communityComments.stream()
                .map(communityComment -> mapToDTO(communityComment, new CommunityCommentDTO()))
                .toList();
    }

    public CommunityCommentDTO get(final Long commentId) {
        return communityCommentRepository.findById(commentId)
                .map(communityComment -> mapToDTO(communityComment, new CommunityCommentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CommunityCommentDTO communityCommentDTO) {
        final CommunityComment communityComment = new CommunityComment();
        mapToEntity(communityCommentDTO, communityComment);
        return communityCommentRepository.save(communityComment).getCommentId();
    }

    public void update(final Long commentId, final CommunityCommentDTO communityCommentDTO) {
        final CommunityComment communityComment = communityCommentRepository.findById(commentId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(communityCommentDTO, communityComment);
        communityCommentRepository.save(communityComment);
    }

    public void delete(final Long commentId) {
        communityCommentRepository.deleteById(commentId);
    }

    private CommunityCommentDTO mapToDTO(final CommunityComment communityComment,
            final CommunityCommentDTO communityCommentDTO) {
        communityCommentDTO.setCommentId(communityComment.getCommentId());
        communityCommentDTO.setMemberId(communityComment.getMemberId());
        communityCommentDTO.setComment(communityComment.getComment());
        communityCommentDTO.setCreatedAt(communityComment.getCreatedAt());
        communityCommentDTO.setActivated(communityComment.getActivated());
        communityCommentDTO.setCount(communityComment.getCount());
        communityCommentDTO.setPost(communityComment.getPost() == null ? null : communityComment.getPost().getPostId());
        return communityCommentDTO;
    }

    private CommunityComment mapToEntity(final CommunityCommentDTO communityCommentDTO,
            final CommunityComment communityComment) {
        communityComment.setMemberId(communityCommentDTO.getMemberId());
        communityComment.setComment(communityCommentDTO.getComment());
        communityComment.setCreatedAt(communityCommentDTO.getCreatedAt());
        communityComment.setActivated(communityCommentDTO.getActivated());
        communityComment.setCount(communityCommentDTO.getCount());
        final CommunityPost post = communityCommentDTO.getPost() == null ? null : communityPostRepository.findById(communityCommentDTO.getPost())
                .orElseThrow(() -> new NotFoundException("post not found"));
        communityComment.setPost(post);
        return communityComment;
    }

}
