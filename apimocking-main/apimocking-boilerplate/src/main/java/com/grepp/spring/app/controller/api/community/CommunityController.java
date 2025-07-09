package com.grepp.spring.app.controller.api.community;

import com.grepp.spring.app.model.community.dto.CommunityCommentCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityCommentResponse;
import com.grepp.spring.app.model.community.dto.CommunityTopPostResponse;
import com.grepp.spring.app.model.community.dto.CommunityPostCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityPostDetailResponse;
import com.grepp.spring.app.model.community.dto.CommunityPostUpdateRequest;
import com.grepp.spring.app.model.community.dto.CommunityUserInfoResponse;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/community", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommunityController {

    List<CommunityPostDetailResponse> posts = List.of(
        new CommunityPostDetailResponse(0, "챌린지", "오늘도 0원 쓰기!", "2025-07-03T14:20:00",
            "물만 마시고 살았어요!", List.of("water.jpg"), 4, 55, true,
            true, false, "닉네임1", "소비 단식러","user1.jpg"),
        new CommunityPostDetailResponse(1, "나가게", "점심 착한식당 인증!", "2025-07-02T12:10:00",
            "회사 근처 착한 식당 방문했습니다.", List.of("lunch.jpg"), 2, 30, false,
            false, true, "닉네임2", "예측왕","user2.jpg"),
        new CommunityPostDetailResponse(2, "자유 게시판", "지출 내역 기록 시작!", "2025-07-01T09:05:00",
            "가계부 기록 시작했어요. 절약 의식 높아지는 중!", List.of(), 5, 40, true,
            false, false, "닉네임3", "기록장인","user3.jpg"),
        new CommunityPostDetailResponse(3, "챌린지", "홈카페 3일차", "2025-06-30T18:30:00",
            "집에서 커피 내려 먹었어요", List.of("coffee.jpg"), 3, 22, false,
            true, true, "닉네임4", "홈카페 마스터","user4.jpg"),
        new CommunityPostDetailResponse(4, "나가게", "이 집 잘하네", "2025-06-29T10:20:00",
            "집 앞 착한 식당에서 삼겹살 먹고 왔습니다!", List.of("gogi.jpg"), 6, 45, true,
            true, true, "닉네임5", "소통왕", "user5.jpg")
    );

    CommunityUserInfoResponse myInfo = new CommunityUserInfoResponse(
        "닉네임1",
        "image1.jpg",
        "소비 단식러",
        "누더기"
    );

    List<String> categories = List.of("나가게", "챌린지", "자유 게시판");

    List<CommunityCommentResponse> comments = List.of(
        new CommunityCommentResponse(0, "절약 멋져요!", "닉네임6", "profile1.jpg", "2025-07-03T15:00:00",
            "2025-07-03T15:30:00"),
        new CommunityCommentResponse(1, "저도 시작해볼게요!", "닉네임7", "profile2.jpg", "2025-07-03T16:00:00",
            "2025-07-03T16:00:00")
    );

    List<CommunityTopPostResponse> topPosts = List.of(
        new CommunityTopPostResponse(0, "닉네임8","영수증인증이다",  "영수증 찐 기록러","2025-07-03T14:20:00"),
        new CommunityTopPostResponse(1, "닉네임9","걷기 운동 중",  "튼튼한 다리 소유자", "2025-07-02T12:10:00"),
        new CommunityTopPostResponse(2, "닉네임10","냉장고 털고 있어요",  "냉털 요리왕", "2025-07-01T09:05:00"),
        new CommunityTopPostResponse(3, "닉네임11","만원으로 하루 살아봤어요",  "만원의 행복", "2025-06-30T18:30:00"),
        new CommunityTopPostResponse(4, "닉네임12","월급 인증이요",  "내 돈 관리자", "2025-06-29T10:20:00")
    );

    @GetMapping("/me")
    @Operation(summary = "커뮤니티 로그인 유저 정보 조회")
    public ResponseEntity<ApiResponse<CommunityUserInfoResponse>> getMyInfo() {
        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(myInfo));
    }

    @GetMapping("/posts")
    @Operation(summary = "커뮤니티 게시글 카테고리별 조회", description = "카테고리 : 나가게, 챌린지, 자유 게시판")
    public ResponseEntity<ApiResponse<List<CommunityPostDetailResponse>>> getPostsByCategory(
        @RequestParam String category) {

        if (!categories.contains(category)) {
            return ResponseEntity
                .status(ResponseCode.BAD_REQUEST.status())
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
        }

        List<CommunityPostDetailResponse> result = posts.stream()
            .filter(p -> p.category().equals(category))
            .toList();

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(result));
    }

    @PostMapping("/posts")
    @Operation(summary = "커뮤니티 게시글 생성")
    public ResponseEntity<ApiResponse<Map<String, String>>> createPost(
        @RequestBody @Valid CommunityPostCreateRequest request
    ) {

        Map<String, String> response = new HashMap<>();
        response.put("message", "게시글이 생성되었습니다.");

        return ResponseEntity
            .status(ResponseCode.CREATED.status())
            .body(ApiResponse.successToCreate(response));
    }

    @PatchMapping("/posts/{post-id}")
    @Operation(summary = "커뮤니티 게시글 수정")
    public ResponseEntity<ApiResponse<Map<String, String>>> updatePost(
        @PathVariable("post-id") int id,
        @RequestBody CommunityPostUpdateRequest request
    ) {

        if (id < 0 || id > 4) {
            return ResponseEntity
                .status(ResponseCode.NOT_FOUND.status())
                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "게시글이 수정되었습니다.");

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

    @PatchMapping("/posts/{post-id}/delete")
    @Operation(summary = "커뮤니티 게시글 삭제")
    public ResponseEntity<ApiResponse<Map<String, String>>> togglePostActive(
        @PathVariable("post-id") int id
    ) {

        if (id < 0 || id > 4) {
            return ResponseEntity
                .status(ResponseCode.NOT_FOUND.status())
                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "게시글이 삭제되었습니다.");

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

    @GetMapping("/posts/{post-id}/comments")
    @Operation(summary = "커뮤니티 게시글별 댓글 조회")
    public ResponseEntity<ApiResponse<List<CommunityCommentResponse>>> getCommentsByPostId(
        @PathVariable("post-id") int id
    ) {

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(comments));
    }

    @PostMapping("/posts/{post-id}/comments")
    @Operation(summary = "커뮤니티 게시글 댓글 작성")
    public ResponseEntity<ApiResponse<Map<String, String>>> createComment(
        @PathVariable("post-id") int id,
        @RequestBody @Valid CommunityCommentCreateRequest request
    ) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "댓글이 생성되었습니다.");

        if (id < 0 || id > 4) {
            return ResponseEntity
                .status(ResponseCode.NOT_FOUND.status())
                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }

        return ResponseEntity
            .status(ResponseCode.CREATED.status())
            .body(ApiResponse.successToCreate(response));
    }

    @PatchMapping("/comments/{comment-id}/delete")
    @Operation(summary = "커뮤니티 댓글 삭제")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteComment(
        @PathVariable("comment-id") int id
    ) {

        if (id < 0 || id > 4) {
            return ResponseEntity
                .status(ResponseCode.NOT_FOUND.status())
                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "댓글이 삭제되었습니다.");

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

    @PatchMapping("/posts/{post-id}/like")
    @Operation(summary = "커뮤니티 게시글 좋아요 활성화/비활성화", description = "현재는 '좋아요가 등록되었습니다'만 뜹니다")
    public ResponseEntity<ApiResponse<Map<String, String>>> toggleLike(
        @PathVariable("post-id") int id
    ) {

        if (id < 0 || id > 4) {
            return ResponseEntity
                .status(ResponseCode.NOT_FOUND.status())
                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "좋아요가 등록되었습니다.");

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

    @PatchMapping("/posts/{post-id}/bookmark")
    @Operation(summary = "커뮤니티 게시글 북마크 활성화/비활성화", description = "현재는 '북마크가 등록되었습니다'만 뜹니다")
    public ResponseEntity<ApiResponse<Map<String, String>>> toggleBookmark(
        @PathVariable("post-id") int id
    ) {
        if (id < 0 || id > 4) {
            return ResponseEntity
                .status(ResponseCode.NOT_FOUND.status())
                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "북마크가 등록되었습니다.");

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

    @GetMapping("/posts/top")
    @Operation(summary = "커뮤니티 인기 게시글 조회")
    public ResponseEntity<ApiResponse<List<CommunityTopPostResponse>>> getTopPosts() {
        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(topPosts));
    }

}
