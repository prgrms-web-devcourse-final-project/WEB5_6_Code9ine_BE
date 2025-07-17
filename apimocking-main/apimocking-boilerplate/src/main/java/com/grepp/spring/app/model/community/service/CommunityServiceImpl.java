package com.grepp.spring.app.model.community.service;

import com.grepp.spring.app.model.challenge.code.ChallengeCategory;
import com.grepp.spring.app.model.challenge.code.CommunityCategory;
import com.grepp.spring.app.model.community.domain.CommunityPost;
import com.grepp.spring.app.model.community.dto.CommunityPostCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityPostDetailResponse;
import com.grepp.spring.app.model.community.dto.CommunityPostUpdateRequest;
import com.grepp.spring.app.model.community.dto.CommunityUserInfoResponse;
import com.grepp.spring.app.model.community.repos.CommunityBookmarkRepository;
import com.grepp.spring.app.model.community.repos.CommunityCommentRepository;
import com.grepp.spring.app.model.community.repos.CommunityLikeRepository;
import com.grepp.spring.app.model.community.repos.CommunityRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.service.MemberService;
import com.grepp.spring.app.model.post_image.domain.PostImage;
import com.grepp.spring.app.model.post_image.repos.PostImageRepository;
import com.grepp.spring.app.model.post_image.service.PostImageService;
import com.grepp.spring.infra.payload.PageParam;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityServiceImpl implements CommunityService {

    private final PostImageService postImageService;
    private final MemberService memberService;
    private final CommunityRepository communityRepository;
    private final CommunityCommentRepository commentRepository;
    private final CommunityLikeRepository likeRepository;
    private final CommunityBookmarkRepository bookmarkRepository;
    private final PostImageRepository postImageRepository;

    // 커뮤니티 로그인 유저 정보
    @Override
    public CommunityUserInfoResponse getMyInfo(Long memberId) {
        Member member = memberService.getMemberById(memberId);

        return new CommunityUserInfoResponse(
            member.getMemberId(),
            member.getNickname(),
            member.getProfileImage(),
            member.getEquippedTitle().getName(),
            // TODO : member 엔티티 수정되면 다시 수정해야함
            member.getLevel() != null ? member.getLevel() : 0
        );
    }

    // 커뮤니티 게시글 생성
    @Override
    public void createPost(CommunityPostCreateRequest request, Member member) {
        CommunityPost post = CommunityPost.builder()
            .title(request.title())
            .content(request.content())
            .category(CommunityCategory.valueOf(request.category()))
            .challenge(request.challengeCategory() != null ? ChallengeCategory.valueOf(request.challengeCategory()) : null)
            .member(member)
            .build();

        communityRepository.save(post);

        List<String> imgUrls = request.imageUrls();
        if (imgUrls != null && !imgUrls.isEmpty()) {
            List<PostImage> images = new ArrayList<>();
            for (int i = 0; i < imgUrls.size(); i++) {
                images.add(PostImage.builder()
                    .post(post)
                    .imageUrl(imgUrls.get(i))
                    .sortOrder(i)
                    .build());
            }
            postImageRepository.saveAll(images);
        }
    }

    // 커뮤니티 게시글 카테고리별 조회
    @Override
    public List<CommunityPostDetailResponse> getPostsByCategory(String category, PageParam pageParam, Long memberId) {
        CommunityCategory categoryEnum = CommunityCategory.valueOf(category);
        Pageable pageable = pageParam.toPageable();
        Page<CommunityPost> posts =  communityRepository.findByCategoryAndActivatedIsTrue(categoryEnum, pageable);

        return posts.stream()
            .map(post -> {
                Long postId = post.getPostId();
                int commentCount = commentRepository.countCommentByPostId(postId);
                int likeCount = likeRepository.countLikeByPostId(postId);
                boolean isLiked = likeRepository.existsByPost_PostIdAndMember_MemberId(postId, memberId);
                boolean isBookmarked = bookmarkRepository.existsByPost_PostIdAndMember_MemberId(postId, memberId);
                // TODO : 챌린지 달성 여부는 챌린지 repository 구현된 이후 추가

                return new CommunityPostDetailResponse(
                    post.getPostId(),
                    post.getMember().getMemberId(),
                    post.getCategory().name(),
                    post.getChallenge() != null ? post.getChallenge().name() : null,
                    post.getTitle(),
                    post.getCreatedAt().toString(),
                    post.getContent(),
                    post.getImages().stream().map(PostImage::getImageUrl).toList(),
                    commentCount,
                    likeCount,
                    isLiked,
                    isBookmarked,
                    false,
                    post.getMember().getNickname(),
                    // TODO : member 엔티티 수정되면 다시 수정해야함
                    post.getMember().getEquippedTitle() != null ? post.getMember().getEquippedTitle().getName() : null,
                    post.getMember().getLevel() != null ? post.getMember().getLevel() : 0,
                    post.getMember().getProfileImage()
                );
        })
            .collect(Collectors.toList());
    }

    // 게시글 수정
    @Override
    @Transactional
    public void updatePost(Long postId, CommunityPostUpdateRequest request, Long memberId) {
        // 존재하는 게시글인지 검증
        CommunityPost post = communityRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 해당 게시글 작성자인지 검증
        if (!post.getMember().getMemberId().equals(memberId)) {
            throw new AuthorizationDeniedException("게시글 수정 권한이 없습니다.");
        }

        // 변경되는 값이 존재할 때만 변경
        if (request.title() != null && !request.title().isBlank()) {
            post.setTitle(request.title());
        }

        if (request.content() != null && !request.content().isBlank()) {
            post.setContent(request.content());
        }

        if (request.category() != null) {
            post.setCategory(CommunityCategory.valueOf(request.category()));
        }

        if (request.challengeCategory() != null) {
            post.setChallenge(ChallengeCategory.valueOf(request.challengeCategory()));
        } else {
            post.setChallenge(null);
        }

        postImageService.updatePostImages(post, request.imageUrls());
    }

    // 게시글 삭제(soft delete)
    @Override
    public void deletePost(Long postId, Long memberId) {
        // 존재하는 게시글인지 검증
        CommunityPost post = communityRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

        // 해당 게시글 작성자인지 검증
        if (!post.getMember().getMemberId().equals(memberId)) {
            throw new AuthorizationDeniedException("게시물 삭제 권한이 없습니다.");
        }

        post.unActivated();
    }

}
