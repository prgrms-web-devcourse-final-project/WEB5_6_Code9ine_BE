package com.grepp.spring.app.controller.api.mock.community;

import com.grepp.spring.app.model.community.dto.CommunityCommentCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityCommentResponse;
import com.grepp.spring.app.model.community.dto.CommunityTopPostResponse;
import com.grepp.spring.app.model.community.dto.CommunityPostCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityPostDetailResponse;
import com.grepp.spring.app.model.community.dto.CommunityPostUpdateRequest;
import com.grepp.spring.app.model.community.dto.CommunityUserInfoResponse;
import com.grepp.spring.infra.payload.PageParam;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Profile;
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
@Profile("mock")
@RequestMapping(value = "/api/community", produces = MediaType.APPLICATION_JSON_VALUE)
public class MockCommunityController {

    List<CommunityPostDetailResponse> mockPosts = List.of(
        new CommunityPostDetailResponse(0L, 1L,"챌린지", "챌린지1","게시물 제목0", "2025-07-10T11:32:00",
            "게시글 내용0", List.of("image0.jpg", "image1.jpg"), 3, 17, true,
            false, true, "작성자 닉네임0", "칭호0", 1, "profile0.jpg"),

        new CommunityPostDetailResponse(1L, 2L, "나가게", null,"게시물 제목1", "2025-07-09T14:55:00",
            "게시글 내용1", List.of("image2.jpg"), 1, 8, false,
            true, false, "작성자 닉네임1", "칭호1", 2, "profile1.jpg"),

        new CommunityPostDetailResponse(2L, 3L, "자유 게시판", null, "게시물 제목2", "2025-07-10T08:12:00",
            "게시글 내용2", List.of("image3.jpg", "image4.jpg", "image5.jpg"), 7, 23, true,
            true, true, "작성자 닉네임2", "칭호2", 3, "profile2.jpg"),

        new CommunityPostDetailResponse(3L, 4L, "챌린지", "챌린지1", "게시물 제목3", "2025-07-09T19:27:00",
            "게시글 내용3", List.of(), 0, 5, false,
            false, false, "작성자 닉네임3", "칭호3", 4, "profile3.jpg"),

        new CommunityPostDetailResponse(4L, 5L, "나가게", null, "게시물 제목4", "2025-07-10T13:45:00",
            "게시글 내용4", List.of("image6.jpg"), 2, 10, true,
            false, true, "작성자 닉네임4", "칭호4", 5, "profile4.jpg"),

        new CommunityPostDetailResponse(5L, 6L, "자유 게시판", null, "게시물 제목5", "2025-07-09T07:55:00",
            "게시글 내용5", List.of("image7.jpg", "image8.jpg"), 5, 32, false,
            true, false, "작성자 닉네임5", "칭호5", 6, "profile5.jpg"),

        new CommunityPostDetailResponse(6L, 7L, "챌린지", "챌린지1", "게시물 제목6", "2025-07-10T10:22:00",
            "게시글 내용6", List.of("image9.jpg"), 4, 11, true,
            true, true, "작성자 닉네임6", "칭호6", 7, "profile6.jpg"),

        new CommunityPostDetailResponse(7L, 8L, "나가게", null, "게시물 제목7", "2025-07-09T16:40:00",
            "게시글 내용7", List.of("image10.jpg", "image11.jpg", "image12.jpg"), 6, 27, true,
            false, true, "작성자 닉네임7", "칭호7", 8, "profile7.jpg"),

        new CommunityPostDetailResponse(8L, 9L, "자유 게시판", null, "게시물 제목8", "2025-07-10T09:10:00",
            "게시글 내용8", List.of(), 0, 0, false,
            false, false, "작성자 닉네임8", "칭호8", 9, "profile8.jpg"),

        new CommunityPostDetailResponse(9L, 10L, "챌린지", "챌린지1", "게시물 제목9", "2025-07-10T12:05:00",
            "게시글 내용9", List.of("image13.jpg"), 2, 18, true,
            true, true, "작성자 닉네임9", "칭호9", 10, "profile9.jpg"),

        new CommunityPostDetailResponse(10L, 11L, "나가게", null, "게시물 제목10", "2025-07-09T20:20:00",
            "게시글 내용10", List.of("image14.jpg", "image15.jpg"), 3, 12, false,
            false, true, "작성자 닉네임10", "칭호10", 11, "profile10.jpg"),

        new CommunityPostDetailResponse(11L, 12L, "자유 게시판", null, "게시물 제목11", "2025-07-10T15:30:00",
            "게시글 내용11", List.of("image16.jpg"), 1, 4, true,
            false, false, "작성자 닉네임11", "칭호11", 12, "profile11.jpg")
    );

    CommunityUserInfoResponse myInfo = new CommunityUserInfoResponse(
        11L,
        "닉네임12",
        "profile12.jpg",
        "칭호12",
        1
    );

    List<String> categories = List.of("나가게", "챌린지", "자유 게시판");

    List<CommunityCommentResponse> comments = List.of(
        new CommunityCommentResponse(0L, 0L,"댓글 내용0", "댓글 작성자0", "profile0.jpg", "칭호0", 1,
            "2025-07-10T09:10:00", "2025-07-10T09:20:00"),
        new CommunityCommentResponse(1L, 1L,"댓글 내용1", "댓글 작성자1", "profile1.jpg", "칭호1", 2,
            "2025-07-10T10:15:00", "2025-07-10T10:45:00"),
        new CommunityCommentResponse(2L, 1L,"댓글 내용2", "댓글 작성자2", "profile2.jpg", "칭호2", 4,
            "2025-07-10T08:00:00", "2025-07-10T08:05:00"),
        new CommunityCommentResponse(3L, 1L,"댓글 내용3", "댓글 작성자3", "profile3.jpg", "칭호3", 8,
            "2025-07-10T11:00:00", "2025-07-10T11:30:00"),
        new CommunityCommentResponse(4L, 1L,"댓글 내용4", "댓글 작성자4", "profile4.jpg", "칭호4", 1,
            "2025-07-10T13:05:00", "2025-07-10T13:25:00"),
        new CommunityCommentResponse(5L, 1L,"댓글 내용5", "댓글 작성자5", "profile5.jpg", "칭호5", 5,
            "2025-07-10T07:40:00", "2025-07-10T07:45:00"),
        new CommunityCommentResponse(6L, 1L,"댓글 내용6", "댓글 작성자6", "profile6.jpg", "칭호6", 7,
            "2025-07-10T14:10:00", "2025-07-10T14:40:00"),
        new CommunityCommentResponse(7L, 1L,"댓글 내용7", "댓글 작성자7", "profile7.jpg", "칭호7", 4,
            "2025-07-10T15:00:00", "2025-07-10T15:30:00"),
        new CommunityCommentResponse(8L, 1L,"댓글 내용8", "댓글 작성자8", "profile8.jpg", "칭호8", 4,
            "2025-07-10T09:20:00", "2025-07-10T09:50:00"),
        new CommunityCommentResponse(9L, 1L,"댓글 내용9", "댓글 작성자9", "profile9.jpg", "칭호9", 1,
            "2025-07-10T12:00:00", "2025-07-10T12:15:00"),
        new CommunityCommentResponse(10L, 1L,"댓글 내용10", "댓글 작성자10", "profile10.jpg", "칭호10", 4,
            "2025-07-10T12:10:00", "2025-07-10T12:45:00")
    );

    List<CommunityTopPostResponse> topPosts = List.of(
        new CommunityTopPostResponse(5L, "작성자 닉네임5", "칭호5", 1, "profile5.jpg", "게시물 제목5",
            "2025-07-09T07:55:00"),
        new CommunityTopPostResponse(7L, "작성자 닉네임7", "칭호7", 2, "profile7.jpg", "게시물 제목7",
            "2025-07-09T16:40:00"),
        new CommunityTopPostResponse(2L, "작성자 닉네임2", "칭호2", 3, "profile2.jpg", "게시물 제목2",
            "2025-07-10T08:12:00"),
        new CommunityTopPostResponse(9L, "작성자 닉네임9", "칭호9", 1, "profile9.jpg", "게시물 제목9",
            "2025-07-10T12:05:00"),
        new CommunityTopPostResponse(0L, "작성자 닉네임0", "칭호0", 4, "profile0.jpg", "게시물 제목0",
            "2025-07-10T11:32:00"),
        new CommunityTopPostResponse(10L, "작성자 닉네임10", "칭호10", 5, "profile10.jpg", "게시물 제목10",
            "2025-07-09T20:20:00"),
        new CommunityTopPostResponse(4L, "작성자 닉네임4", "칭호4", 7, "profile4.jpg", "게시물 제목4",
            "2025-07-10T13:45:00"),
        new CommunityTopPostResponse(6L, "작성자 닉네임6", "칭호6", 1, "profile6.jpg", "게시물 제목6",
            "2025-07-10T10:22:00"),
        new CommunityTopPostResponse(11L, "작성자 닉네임11", "칭호11", 4, "profile11.jpg", "게시물 제목11",
            "2025-07-10T15:30:00"),
        new CommunityTopPostResponse(1L, "작성자 닉네임1", "칭호1", 8, "profile1.jpg", "게시물 제목1",
            "2025-07-09T14:55:00")
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
        @RequestParam String category,
        @ParameterObject PageParam pageParam
    ) {

        if (!categories.contains(category)) {
            return ResponseEntity
                .status(ResponseCode.BAD_REQUEST.status())
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
        }

        List<CommunityPostDetailResponse> result = mockPosts.stream()
            .filter(p -> p.category().equals(category))
            .toList();

        int page = pageParam.getPage();
        int size = pageParam.getSize();

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, result.size());

        if (fromIndex >= result.size()) {
            return ResponseEntity
                .status(ResponseCode.BAD_REQUEST.status())
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
        }

        List<CommunityPostDetailResponse> paged = result.subList(fromIndex, toIndex);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(paged));
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