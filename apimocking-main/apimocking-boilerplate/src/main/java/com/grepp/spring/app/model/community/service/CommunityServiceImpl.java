package com.grepp.spring.app.model.community.service;

import com.grepp.spring.app.model.challenge.code.ChallengeCategory;
import com.grepp.spring.app.model.challenge.code.CommunityCategory;
import com.grepp.spring.app.model.community.domain.CommunityBookmark;
import com.grepp.spring.app.model.community.domain.CommunityComment;
import com.grepp.spring.app.model.community.domain.CommunityLike;
import com.grepp.spring.app.model.community.domain.CommunityPost;
import com.grepp.spring.app.model.community.dto.CommunityCommentCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityCommentResponse;
import com.grepp.spring.app.model.community.dto.CommunityPostCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityPostDetailResponse;
import com.grepp.spring.app.model.community.dto.CommunityPostUpdateRequest;
import com.grepp.spring.app.model.community.dto.CommunityTopPostResponse;
import com.grepp.spring.app.model.community.dto.CommunityUserInfoResponse;
import com.grepp.spring.app.model.community.repos.CommunityBookmarkRepository;
import com.grepp.spring.app.model.community.repos.CommunityCommentRepository;
import com.grepp.spring.app.model.community.repos.CommunityLikeRepository;
import com.grepp.spring.app.model.community.repos.CommunityRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.service.MemberService;
import com.grepp.spring.app.model.notification.service.NotificationService;
import com.grepp.spring.app.model.post_image.domain.PostImage;
import com.grepp.spring.app.model.post_image.repos.PostImageRepository;
import com.grepp.spring.app.model.post_image.service.PostImageService;
import com.grepp.spring.infra.error.exceptions.BadRequestException;
import com.grepp.spring.infra.payload.PageParam;
import com.grepp.spring.util.NotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityServiceImpl implements CommunityService {

    private final PostImageService postImageService;
    private final MemberService memberService;
    private final NotificationService notificationService;
    private final CommunityRepository communityRepository;
    private final CommunityCommentRepository commentRepository;
    private final CommunityLikeRepository likeRepository;
    private final CommunityBookmarkRepository bookmarkRepository;
    private final PostImageRepository postImageRepository;

    // 커뮤니티 로그인 유저 정보
    @Override
    @Transactional(readOnly = true)
    public CommunityUserInfoResponse getMyInfo(Long memberId) {
        Member member = memberService.getMemberById(memberId);

        return new CommunityUserInfoResponse(
            member.getMemberId(),
            member.getNickname(),
            member.getProfileImage(),
            member.getEquippedTitle().getName(),
            member.getLevel()
        );
    }

    // 커뮤니티 게시글 생성
    @Override
    @Transactional
    public void createPost(CommunityPostCreateRequest request, Member member) {
        CommunityPost post = CommunityPost.builder()
            .title(request.title())
            .content(request.content())
            .category(CommunityCategory.valueOf(request.category()))
            .challenge(request.challengeCategory() != null ? ChallengeCategory.valueOf(request.challengeCategory()) : null)
            .member(member)
            .build();

        communityRepository.save(post);

        List<String> imageUrls = request.imageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            postImageService.addPostImages(post, imageUrls);
        }
    }

    // 커뮤니티 게시글 카테고리별 조회
    @Override
    @Transactional(readOnly = true)
    public List<CommunityPostDetailResponse> getPostsByCategory(String category, PageParam pageParam, Long memberId) {
        CommunityCategory categoryEnum = CommunityCategory.valueOf(category);
        Pageable pageable = pageParam.toPageable(Sort.by(Sort.Direction.DESC,"createdAt"));
        Page<CommunityPost> posts =  communityRepository.findByCategoryAndActivatedIsTrue(categoryEnum, pageable);

        return posts.stream()
            .map(post -> {
                Long postId = post.getPostId();
                int commentCount = commentRepository.countByPost_PostIdAndActivatedTrue(postId);
                int likeCount = likeRepository.countLikeByPost_PostIdAndActivatedTrue(postId);
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
                    post.getMember().getEquippedTitle() != null ? post.getMember().getEquippedTitle().getName() : null,
                    post.getMember().getLevel(),
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
        CommunityPost post = getActivatedPost(postId);

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
    @Transactional
    public void deletePost(Long postId, Long memberId) {
        // 존재하는 게시글인지 검증
        CommunityPost post = getActivatedPost(postId);

        // 해당 게시글 작성자인지 검증
        if (!post.getMember().getMemberId().equals(memberId)) {
            throw new AuthorizationDeniedException("게시물 삭제 권한이 없습니다.");
        }

        post.unActivated();
    }

    // 게시물 단건 조회
    @Override
    @Transactional(readOnly = true)
    public CommunityPostDetailResponse getPostById(Long postId, Long memberId) {

        // 존재하는 게시물인지 검증
        CommunityPost post = getActivatedPost(postId);

        int commentCount = commentRepository.countByPost_PostIdAndActivatedTrue(postId);
        int likeCount = likeRepository.countLikeByPost_PostIdAndActivatedTrue(postId);
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
            post.getMember().getEquippedTitle() != null ? post.getMember().getEquippedTitle().getName() : null,
            post.getMember().getLevel(),
            post.getMember().getProfileImage()
        );
    }

    // 게시글 댓글 조회
    @Override
    @Transactional(readOnly = true)
    public List<CommunityCommentResponse> getCommentsByPostId(Long postId) {

        // 존재하는 게시물인지 검증
        getActivatedPost(postId);

        List<CommunityComment> comments = commentRepository.findByPost_PostIdAndActivatedTrue(postId);

        return comments.stream()
            .map(comment -> new CommunityCommentResponse(
                comment.getCommentId(),
                comment.getMember().getMemberId(),
                comment.getContent(),
                comment.getMember().getNickname(),
                comment.getMember().getProfileImage(),
                comment.getMember().getEquippedTitle() != null ? comment.getMember().getEquippedTitle().getName() : null,
                comment.getMember().getLevel(),
                comment.getCreatedAt().toString(),
                comment.getModifiedAt().toString()
            ))
            .toList();
    }

    // 게시물 댓글 작성
    @Override
    @Transactional
    public void createComment(Long postId, CommunityCommentCreateRequest request, Member member) {

        // 존재하는 게시물인지 검증
        CommunityPost post = getActivatedPostWithLock(postId);

        // 댓글 생성 및 저장
        CommunityComment comment = CommunityComment.builder()
            .post(post)
            .member(member)
            .content(request.content())
            .build();

        commentRepository.save(comment);
        post.setCommentCount(post.getCommentCount() + 1);

        // 알림 생성
        Long receiverId = post.getMember().getMemberId();
        Long senderId = member.getMemberId();

        notificationService.createNotification(receiverId, senderId, "COMMENT");
    }

    // 게시물 댓글 삭제
    @Override
    @Transactional
    public void deleteComment(Long commentId, Long memberId) {

        // 존재하는 댓글인지 검증
        CommunityComment comment = getActivatedComment(commentId);

        // 해당 댓글 작성자인지 검증
        if (!comment.getMember().getMemberId().equals(memberId)) {
            throw new AuthorizationDeniedException("댓글 삭제 권한이 없습니다.");
        }

        comment.unActivated();
        CommunityPost post = getActivatedPostWithLock(comment.getPost().getPostId());
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
    }

    // 게시물 좋아요 활성화/비활성화
    @Override
    @Transactional
    public boolean toggleLike(Long postId, Long memberId) {
        // 존재하는 게시물인지 검증
        CommunityPost post = getActivatedPostWithLock(postId);

        // 좋아요 활성화 여부 확인
        Optional<CommunityLike> existingLike = likeRepository.findByPost_PostIdAndMember_MemberId(postId, memberId);

        if (existingLike.isPresent()) {
            CommunityLike like = existingLike.get();
            if (like.getActivated()) {
                // 활성화 → 비활성화
                like.unActivated();
                post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                return false;
            } else {
                // 비활성화 → 활성화
                like.activate();
                post.setLikeCount(post.getLikeCount() + 1);
                // 알림 생성
                notificationService.createNotification(post.getMember().getMemberId(), memberId, "LIKE");
                return true;
            }
        } else {
            // 좋아요가 없는 경우 새로 생성
            CommunityLike newLike = CommunityLike.builder()
                .post(post)
                .member(memberService.getMemberById(memberId))
                .build();
            likeRepository.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);
            notificationService.createNotification(post.getMember().getMemberId(), memberId, "LIKE");
            return true;
        }
    }

    // 게시물 북마크 활성화/비활성화
    @Override
    @Transactional
    public boolean toggleBookmark(Long postId, Long memberId) {
        // 존재하는 게시물인지 검증
        CommunityPost post = getActivatedPost(postId);

        // 북마크 활성화 여부 확인
        Optional<CommunityBookmark> existingBookmark = bookmarkRepository.findByPost_PostIdAndMember_MemberId(postId, memberId);

        if (existingBookmark.isPresent()) {
            CommunityBookmark bookmark = existingBookmark.get();
            if (bookmark.getActivated()) {
                // 활성화 → 비활성화
                bookmark.unActivated();
                return false;
            } else {
                // 비활성화 → 활성화
                bookmark.activate();
                return true;
            }
        } else {
            // 북마크가 없는 경우 새로 생성
            CommunityBookmark newBookmark = CommunityBookmark.builder()
                .post(post)
                .member(memberService.getMemberById(memberId))
                .build();
            bookmarkRepository.save(newBookmark);
            return true;
        }
    }

    // 커뮤니티 인기 게시글 조회
    @Override
    @Transactional(readOnly = true)
    public List<CommunityTopPostResponse> getTopPosts() {
        List<CommunityPost> posts = communityRepository.findTop10ByActivatedIsTrueOrderByLikeCountDesc();

        return posts.stream().map(post -> new CommunityTopPostResponse(
            post.getPostId(),
            post.getMember().getNickname(),
            post.getMember().getEquippedTitle() != null ? post.getMember().getEquippedTitle().getName() : null,
            post.getMember().getLevel(),
            post.getMember().getProfileImage(),
            post.getTitle(),
            post.getCreatedAt().toString(),
            post.getCategory().toString(),
            post.getLikeCount()
        )).toList();
    }

    // 존재하는 게시물인지 검증 메서드(조회)
    private CommunityPost getActivatedPost(Long postId) {
        return communityRepository.findByPostIdAndActivatedIsTrue(postId)
            .orElseThrow(() -> new NotFoundException("존재하지 않거나 삭제된 게시글입니다."));
    }

    // 존재하는 게시물인지 검증 메서드(변경, 락)
    private CommunityPost getActivatedPostWithLock(Long postId) {
        return communityRepository.findActivatedPostWithLock(postId)
            .orElseThrow(() -> new NotFoundException("존재하지 않거나 삭제된 게시글입니다."));
    }

    // 존재하는 댓글인지 검증 메서드
    private CommunityComment getActivatedComment(Long commentId) {
        return commentRepository.findByCommentIdAndActivatedTrue(commentId)
            .orElseThrow(() -> new NotFoundException("존재하지 않거나 이미 삭제된 댓글입니다."));
    }

    // 내가 작성한 게시글 목록 조회 (마이페이지용)
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMyPosts(Long memberId) {
        List<CommunityPost> posts = communityRepository.findByMember_MemberIdAndActivatedIsTrue(memberId);
        
        return posts.stream()
            .map(post -> {
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("postId", post.getPostId());
                postMap.put("title", post.getTitle());
                return postMap;
            })
            .toList();
    }

    // 내가 북마크한 게시글 목록 조회 (마이페이지용)
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getBookmarkedPosts(Long memberId) {
        List<CommunityBookmark> bookmarks = bookmarkRepository.findByMember_MemberIdAndActivatedTrue(memberId);
        
        return bookmarks.stream()
            .map(bookmark -> {
                CommunityPost post = bookmark.getPost();
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("postId", post.getPostId());
                postMap.put("title", post.getTitle());
                return postMap;
            })
            .toList();
    }
}
