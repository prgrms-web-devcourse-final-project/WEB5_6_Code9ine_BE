package com.grepp.spring.app.controller.api.community;

import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.challenge.code.CommunityCategory;
import com.grepp.spring.app.model.community.dto.CommunityPostCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityPostDetailResponse;
import com.grepp.spring.app.model.community.service.CommunityService;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.infra.payload.PageParam;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.Arrays;
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
    private final MemberRepository memberRepository;

//    @GetMapping("/me")
//    @Operation(summary = "커뮤니티 로그인 유저 정보 조회")
//    public ResponseEntity<ApiResponse<CommunityUserInfoResponse>> getMyInfo() {
//        return ResponseEntity
//            .status(ResponseCode.OK.status())
//            .body(ApiResponse.success(myInfo));
//    }

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
            return ResponseEntity
                .status(ResponseCode.BAD_REQUEST.status())
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
        }

        List<CommunityPostDetailResponse> posts = communityService.getPostsByCategory(category, pageParam, principal.getMemberId());

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(posts));
    }

    @PostMapping("/posts")
    @Operation(summary = "커뮤니티 게시글 생성")
    public ResponseEntity<ApiResponse<Map<String, String>>> createPost(
        @RequestBody @Valid CommunityPostCreateRequest request,
        @AuthenticationPrincipal Principal principal
    ) {
        Long memberId = principal.getMemberId();
        log.info("게시물 생성 - member ID : {}", memberId);

        // TODO 추후 member 서비스가 구현되면 수정
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        communityService.createPost(request, member);
        log.info("게시물 생성 - request : {}", request);

        Map<String, String> responseBody = Map.of("message", "게시글이 생성되었습니다.");

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.successToCreate(responseBody));
    }
//
//    @PatchMapping("/posts/{post-id}")
//    @Operation(summary = "커뮤니티 게시글 수정")
//    public ResponseEntity<ApiResponse<Map<String, String>>> updatePost(
//        @PathVariable("post-id") int id,
//        @RequestBody CommunityPostUpdateRequest request
//    ) {
//
//        if (id < 0 || id > 4) {
//            return ResponseEntity
//                .status(ResponseCode.NOT_FOUND.status())
//                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
//        }
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "게시글이 수정되었습니다.");
//
//        return ResponseEntity
//            .status(ResponseCode.OK.status())
//            .body(ApiResponse.success(response));
//    }
//
//    @PatchMapping("/posts/{post-id}/delete")
//    @Operation(summary = "커뮤니티 게시글 삭제")
//    public ResponseEntity<ApiResponse<Map<String, String>>> togglePostActive(
//        @PathVariable("post-id") int id
//    ) {
//
//        if (id < 0 || id > 4) {
//            return ResponseEntity
//                .status(ResponseCode.NOT_FOUND.status())
//                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
//        }
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "게시글이 삭제되었습니다.");
//
//        return ResponseEntity
//            .status(ResponseCode.OK.status())
//            .body(ApiResponse.success(response));
//    }
//
//    @GetMapping("/posts/{post-id}/comments")
//    @Operation(summary = "커뮤니티 게시글별 댓글 조회")
//    public ResponseEntity<ApiResponse<List<CommunityCommentResponse>>> getCommentsByPostId(
//        @PathVariable("post-id") int id
//    ) {
//
//        return ResponseEntity
//            .status(ResponseCode.OK.status())
//            .body(ApiResponse.success(comments));
//    }
//
//    @PostMapping("/posts/{post-id}/comments")
//    @Operation(summary = "커뮤니티 게시글 댓글 작성")
//    public ResponseEntity<ApiResponse<Map<String, String>>> createComment(
//        @PathVariable("post-id") int id,
//        @RequestBody @Valid CommunityCommentCreateRequest request
//    ) {
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "댓글이 생성되었습니다.");
//
//        if (id < 0 || id > 4) {
//            return ResponseEntity
//                .status(ResponseCode.NOT_FOUND.status())
//                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
//        }
//
//        return ResponseEntity
//            .status(ResponseCode.CREATED.status())
//            .body(ApiResponse.successToCreate(response));
//    }
//
//    @PatchMapping("/comments/{comment-id}/delete")
//    @Operation(summary = "커뮤니티 댓글 삭제")
//    public ResponseEntity<ApiResponse<Map<String, String>>> deleteComment(
//        @PathVariable("comment-id") int id
//    ) {
//
//        if (id < 0 || id > 4) {
//            return ResponseEntity
//                .status(ResponseCode.NOT_FOUND.status())
//                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
//        }
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "댓글이 삭제되었습니다.");
//
//        return ResponseEntity
//            .status(ResponseCode.OK.status())
//            .body(ApiResponse.success(response));
//    }
//
//    @PatchMapping("/posts/{post-id}/like")
//    @Operation(summary = "커뮤니티 게시글 좋아요 활성화/비활성화", description = "현재는 '좋아요가 등록되었습니다'만 뜹니다")
//    public ResponseEntity<ApiResponse<Map<String, String>>> toggleLike(
//        @PathVariable("post-id") int id
//    ) {
//
//        if (id < 0 || id > 4) {
//            return ResponseEntity
//                .status(ResponseCode.NOT_FOUND.status())
//                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
//        }
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "좋아요가 등록되었습니다.");
//
//        return ResponseEntity
//            .status(ResponseCode.OK.status())
//            .body(ApiResponse.success(response));
//    }
//
//    @PatchMapping("/posts/{post-id}/bookmark")
//    @Operation(summary = "커뮤니티 게시글 북마크 활성화/비활성화", description = "현재는 '북마크가 등록되었습니다'만 뜹니다")
//    public ResponseEntity<ApiResponse<Map<String, String>>> toggleBookmark(
//        @PathVariable("post-id") int id
//    ) {
//        if (id < 0 || id > 4) {
//            return ResponseEntity
//                .status(ResponseCode.NOT_FOUND.status())
//                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
//        }
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "북마크가 등록되었습니다.");
//
//        return ResponseEntity
//            .status(ResponseCode.OK.status())
//            .body(ApiResponse.success(response));
//    }
//
//    @GetMapping("/posts/top")
//    @Operation(summary = "커뮤니티 인기 게시글 조회")
//    public ResponseEntity<ApiResponse<List<CommunityTopPostResponse>>> getTopPosts() {
//        return ResponseEntity
//            .status(ResponseCode.OK.status())
//            .body(ApiResponse.success(topPosts));
//    }
}