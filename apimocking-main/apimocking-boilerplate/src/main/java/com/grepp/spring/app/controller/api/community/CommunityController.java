package com.grepp.spring.app.controller.api.community;

import com.google.rpc.context.AttributeContext.Auth;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.challenge.code.CommunityCategory;
import com.grepp.spring.app.model.community.dto.CommunityCommentCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityCommentResponse;
import com.grepp.spring.app.model.community.dto.CommunityPostCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityPostDetailResponse;
import com.grepp.spring.app.model.community.dto.CommunityPostUpdateRequest;
import com.grepp.spring.app.model.community.dto.CommunityTopPostResponse;
import com.grepp.spring.app.model.community.dto.CommunityUserInfoResponse;
import com.grepp.spring.app.model.community.service.CommunityService;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.service.MemberService;
import com.grepp.spring.infra.error.exceptions.BadRequestException;
import com.grepp.spring.infra.payload.PageParam;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Profile("!mock")
@RequestMapping(value = "/api/community", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class CommunityController {

    private final CommunityService communityService;
    private final MemberService memberService;

    @GetMapping("/me")
    @Operation(summary = "커뮤니티 로그인 유저 정보 조회")
    public ResponseEntity<ApiResponse<CommunityUserInfoResponse>> getMyInfo(
        @AuthenticationPrincipal Principal principal
    ) {
        Long memberId = principal.getMemberId();
        CommunityUserInfoResponse response = communityService.getMyInfo(memberId);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

    @GetMapping("/posts")
    @Operation(summary = "커뮤니티 게시글 카테고리별 조회", description = "카테고리 : MY_STORE, CHALLENGE, FREE")
    public ResponseEntity<ApiResponse<List<CommunityPostDetailResponse>>> getPostsByCategory(
        @RequestParam String category,
        @ParameterObject PageParam pageParam,
        @AuthenticationPrincipal Principal principal
    ) {
        boolean isValidCategory = Arrays.stream(CommunityCategory.values())
            .anyMatch(c -> c.name().equals(category));

        if (!isValidCategory) {
            throw new BadRequestException("유효하지 않은 카테고리입니다.");
        }

        List<CommunityPostDetailResponse> posts = communityService.getPostsByCategory(category, pageParam, principal.getMemberId());

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(posts));
    }

    @PostMapping("/posts")
    @Operation(summary = "커뮤니티 게시글 생성", description = "카테고리 : MY_STORE, CHALLENGE, FREE<br/> 챌린지 카테고리 : NO_MONEY, KIND_CONSUMER, DETECTIVE, MASTER,COOK_KING ")
    public ResponseEntity<ApiResponse<Map<String, String>>> createPost(
        @RequestBody @Valid CommunityPostCreateRequest request,
        @AuthenticationPrincipal Principal principal
    ) {
        Long memberId = principal.getMemberId();
        log.info("게시물 생성 - member ID : {}", memberId);

        Member member = memberService.getMemberById(memberId);

        communityService.createPost(request, member);
        log.info("게시물 생성 - request : {}", request);

        Map<String, String> responseBody = Map.of("message", "게시글이 생성되었습니다.");

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.successToCreate(responseBody));
    }

    @PatchMapping("/posts/{post-id}")
    @Operation(summary = "커뮤니티 게시글 수정")
    public ResponseEntity<ApiResponse<Map<String, String>>> updatePost(
        @PathVariable("post-id") Long id,
        @RequestBody CommunityPostUpdateRequest request,
        @AuthenticationPrincipal Principal principal
    ) {
        Long memberId = principal.getMemberId();
        communityService.updatePost(id, request, memberId);

        Map<String, String> response = Map.of("message", "게시글이 수정되었습니다.");

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

    @PatchMapping("/posts/{post-id}/delete")
    @Operation(summary = "커뮤니티 게시글 삭제")
    public ResponseEntity<ApiResponse<Map<String, String>>> togglePostActive(
        @PathVariable("post-id") Long id,
        @AuthenticationPrincipal Principal principal
    ) {
        Long memberId = principal.getMemberId();
        communityService.deletePost(id, memberId);

        Map<String, String> response = Map.of("message", "게시글이 삭제되었습니다.");

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

    @GetMapping("/posts/{postId}")
    @Operation(summary = "게시글 단건 조회")
    public ResponseEntity<ApiResponse<CommunityPostDetailResponse>> getPostById(
        @PathVariable Long postId,
        @AuthenticationPrincipal Principal principal
    ) {
        Long memberId = principal.getMemberId();
        CommunityPostDetailResponse result = communityService.getPostById(postId, memberId);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(result));
    }

    @GetMapping("/posts/{post-id}/comments")
    @Operation(summary = "커뮤니티 게시글별 댓글 조회")
    public ResponseEntity<ApiResponse<List<CommunityCommentResponse>>> getCommentsByPostId(
        @PathVariable("post-id") Long id
    ) {
        List<CommunityCommentResponse> response = communityService.getCommentsByPostId(id);

        return ResponseEntity
            .ok(ApiResponse.success(response));
    }

    @PostMapping("/posts/{post-id}/comments")
    @Operation(summary = "커뮤니티 게시글 댓글 작성")
    public ResponseEntity<ApiResponse<Map<String, String>>> createComment(
        @PathVariable("post-id") Long id,
        @RequestBody @Valid CommunityCommentCreateRequest request,
        @AuthenticationPrincipal Principal principal
    ) {
        Long memberId = principal.getMemberId();
        Member member = memberService.getMemberById(memberId);

        communityService.createComment(id, request, member);
        Map<String, String> response = Map.of("message", "댓글이 생성되었습니다.");

        return ResponseEntity
            .status(ResponseCode.CREATED.status())
            .body(ApiResponse.successToCreate(response));
    }

    @PatchMapping("/comments/{comment-id}/delete")
    @Operation(summary = "커뮤니티 댓글 삭제")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteComment(
        @PathVariable("comment-id") Long id,
        @AuthenticationPrincipal Principal principal
    ) {
        Long memberId = principal.getMemberId();
        communityService.deleteComment(id, memberId);

        Map<String, String> response = Map.of("message", "댓글이 삭제되었습니다.");

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

    @PatchMapping("/posts/{post-id}/like")
    @Operation(summary = "커뮤니티 게시글 좋아요 활성화/비활성화")
    public ResponseEntity<ApiResponse<Map<String, String>>> toggleLike(
        @PathVariable("post-id") Long id,
        @AuthenticationPrincipal Principal principal
    ) {
        Long memberId = principal.getMemberId();
        boolean isLiked = communityService.toggleLike(id, memberId);

        String msg = isLiked ? "좋아요가 등록되었습니다." : "좋아요가 해제되었습니다.";
        Map<String, String> response = Map.of("message", msg);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

    @PatchMapping("/posts/{post-id}/bookmark")
    @Operation(summary = "커뮤니티 게시글 북마크 활성화/비활성화")
    public ResponseEntity<ApiResponse<Map<String, String>>> toggleBookmark(
        @PathVariable("post-id") Long id,
        @AuthenticationPrincipal Principal principal
    ) {
        Long memberId = principal.getMemberId();
        boolean isBookmarked = communityService.toggleBookmark(id, memberId);

        String msg = isBookmarked ? "북마크가 등록되었습니다." : "북마크가 해제되었습니다.";
        Map<String, String> response = Map.of("message", msg);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

    @GetMapping("/posts/top")
    @Operation(summary = "커뮤니티 인기 게시글 조회")
    public ResponseEntity<ApiResponse<List<CommunityTopPostResponse>>> getTopPosts() {
        List<CommunityTopPostResponse> topPosts = communityService.getTopPosts();

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(topPosts));
    }


}