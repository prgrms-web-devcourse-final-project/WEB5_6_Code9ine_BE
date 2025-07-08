package com.grepp.spring.app.model.community_post.service;

import com.grepp.spring.app.model.community_bookmark.domain.CommunityBookmark;
import com.grepp.spring.app.model.community_bookmark.repos.CommunityBookmarkRepository;
import com.grepp.spring.app.model.community_comment.domain.CommunityComment;
import com.grepp.spring.app.model.community_comment.repos.CommunityCommentRepository;
import com.grepp.spring.app.model.community_like.domain.CommunityLike;
import com.grepp.spring.app.model.community_like.repos.CommunityLikeRepository;
import com.grepp.spring.app.model.community_post.domain.CommunityPost;
import com.grepp.spring.app.model.community_post.model.CommunityPostDTO;
import com.grepp.spring.app.model.community_post.repos.CommunityPostRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.post_image.domain.PostImage;
import com.grepp.spring.app.model.post_image.repos.PostImageRepository;
import com.grepp.spring.util.NotFoundException;
import com.grepp.spring.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final MemberRepository memberRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityBookmarkRepository communityBookmarkRepository;
    private final PostImageRepository postImageRepository;

    public CommunityPostService(final CommunityPostRepository communityPostRepository,
            final MemberRepository memberRepository,
            final CommunityCommentRepository communityCommentRepository,
            final CommunityLikeRepository communityLikeRepository,
            final CommunityBookmarkRepository communityBookmarkRepository,
            final PostImageRepository postImageRepository) {
        this.communityPostRepository = communityPostRepository;
        this.memberRepository = memberRepository;
        this.communityCommentRepository = communityCommentRepository;
        this.communityLikeRepository = communityLikeRepository;
        this.communityBookmarkRepository = communityBookmarkRepository;
        this.postImageRepository = postImageRepository;
    }

    public List<CommunityPostDTO> findAll() {
        final List<CommunityPost> communityPosts = communityPostRepository.findAll(Sort.by("postId"));
        return communityPosts.stream()
                .map(communityPost -> mapToDTO(communityPost, new CommunityPostDTO()))
                .toList();
    }

    public CommunityPostDTO get(final Long postId) {
        return communityPostRepository.findById(postId)
                .map(communityPost -> mapToDTO(communityPost, new CommunityPostDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CommunityPostDTO communityPostDTO) {
        final CommunityPost communityPost = new CommunityPost();
        mapToEntity(communityPostDTO, communityPost);
        return communityPostRepository.save(communityPost).getPostId();
    }

    public void update(final Long postId, final CommunityPostDTO communityPostDTO) {
        final CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(communityPostDTO, communityPost);
        communityPostRepository.save(communityPost);
    }

    public void delete(final Long postId) {
        communityPostRepository.deleteById(postId);
    }

    private CommunityPostDTO mapToDTO(final CommunityPost communityPost,
            final CommunityPostDTO communityPostDTO) {
        communityPostDTO.setPostId(communityPost.getPostId());
        communityPostDTO.setTitle(communityPost.getTitle());
        communityPostDTO.setContent(communityPost.getContent());
        communityPostDTO.setCreatedAt(communityPost.getCreatedAt());
        communityPostDTO.setUpdatedAt(communityPost.getUpdatedAt());
        communityPostDTO.setActivated(communityPost.getActivated());
        communityPostDTO.setCategory(communityPost.getCategory());
        communityPostDTO.setMember(communityPost.getMember() == null ? null : communityPost.getMember().getMemberId());
        return communityPostDTO;
    }

    private CommunityPost mapToEntity(final CommunityPostDTO communityPostDTO,
            final CommunityPost communityPost) {
        communityPost.setTitle(communityPostDTO.getTitle());
        communityPost.setContent(communityPostDTO.getContent());
        communityPost.setCreatedAt(communityPostDTO.getCreatedAt());
        communityPost.setUpdatedAt(communityPostDTO.getUpdatedAt());
        communityPost.setActivated(communityPostDTO.getActivated());
        communityPost.setCategory(communityPostDTO.getCategory());
        final Member member = communityPostDTO.getMember() == null ? null : memberRepository.findById(communityPostDTO.getMember())
                .orElseThrow(() -> new NotFoundException("member not found"));
        communityPost.setMember(member);
        return communityPost;
    }

    public ReferencedWarning getReferencedWarning(final Long postId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(NotFoundException::new);
        final CommunityComment postCommunityComment = communityCommentRepository.findFirstByPost(communityPost);
        if (postCommunityComment != null) {
            referencedWarning.setKey("communityPost.communityComment.post.referenced");
            referencedWarning.addParam(postCommunityComment.getCommentId());
            return referencedWarning;
        }
        final CommunityLike postCommunityLike = communityLikeRepository.findFirstByPost(communityPost);
        if (postCommunityLike != null) {
            referencedWarning.setKey("communityPost.communityLike.post.referenced");
            referencedWarning.addParam(postCommunityLike.getLikeId());
            return referencedWarning;
        }
        final CommunityBookmark postCommunityBookmark = communityBookmarkRepository.findFirstByPost(communityPost);
        if (postCommunityBookmark != null) {
            referencedWarning.setKey("communityPost.communityBookmark.post.referenced");
            referencedWarning.addParam(postCommunityBookmark.getCBookmarkId());
            return referencedWarning;
        }
        final PostImage postPostImage = postImageRepository.findFirstByPost(communityPost);
        if (postPostImage != null) {
            referencedWarning.setKey("communityPost.postImage.post.referenced");
            referencedWarning.addParam(postPostImage.getImageId());
            return referencedWarning;
        }
        return null;
    }

}
