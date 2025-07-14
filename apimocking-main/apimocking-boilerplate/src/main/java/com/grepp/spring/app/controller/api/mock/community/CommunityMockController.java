package com.grepp.spring.app.controller.api.mock.community;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;
import java.util.*;

@RestController
@Profile("mock")
@RequestMapping(value = "/api/community", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommunityMockController {
    // --- 게시글/댓글/좋아요/북마크/팔로우/오버스펜딩 등 모든 커뮤니티 mock API 통합 ---

    // 예시 데이터 (실제 서비스에서는 DB 연동)
    private static final List<Map<String, Object>> mockPosts = List.of(
            Map.of("postId", 1, "title", "첫 번째 글", "category", "챌린지"),
            Map.of("postId", 2, "title", "나만의 가성비 장소~!", "category", "나가게")
    );

    // 내 북마크 목록
    @GetMapping("/posts/bookmarks")
    public ResponseEntity<BookmarkListResponse> getBookmarks() {
        BookmarkListResponse.Data data = new BookmarkListResponse.Data(List.of(
                Map.of("postId", 3, "title", "나만의 가성비 장소~!")
        ));
        return ResponseEntity.ok(new BookmarkListResponse(2000, "북마크 목록을 조회했습니다.", data));
    }

    // 팔로우한 게시글 목록
    @GetMapping("/posts/follow")
    public ResponseEntity<FollowListResponse> getFollowedPosts() {
        FollowListResponse.Data data = new FollowListResponse.Data(List.of(
                Map.of("postId", 1, "message", "사용자 홍길동이 나를 팔로우했습니다!")
        ));
        return ResponseEntity.ok(new FollowListResponse(2000, "팔로우한 게시글을 조회했습니다.", data));
    }

    // 오버스펜딩 알림/목록
    @GetMapping("/posts/overspending")
    public ResponseEntity<OverspendingListResponse> getOverspending() {
        OverspendingListResponse.Data data = new OverspendingListResponse.Data(List.of(
                Map.of("message", "이번 달 외식비 지출이 많아요!")
        ));
        return ResponseEntity.ok(new OverspendingListResponse(2000, "오버스펜딩 알림을 조회했습니다.", data));
    }

    // --- 기존 CommunityController 주요 엔드포인트 예시 (필요시 추가 구현) ---
    // ... (여기에 게시글, 댓글, 좋아요 등 기존 기능을 static DTO와 함께 추가 구현) ...

    // 내부 static DTO들
    public static class BookmarkListResponse {
        public int code;
        public String message;
        public Data data;
        public BookmarkListResponse(int code, String message, Data data) { this.code = code; this.message = message; this.data = data; }
        public static class Data {
            public List<Map<String, Object>> bookmarks;
            public Data(List<Map<String, Object>> bookmarks) { this.bookmarks = bookmarks; }
        }
    }
    public static class FollowListResponse {
        public int code;
        public String message;
        public Data data;
        public FollowListResponse(int code, String message, Data data) { this.code = code; this.message = message; this.data = data; }
        public static class Data {
            public List<Map<String, Object>> follows;
            public Data(List<Map<String, Object>> follows) { this.follows = follows; }
        }
    }
    public static class OverspendingListResponse {
        public int code;
        public String message;
        public Data data;
        public OverspendingListResponse(int code, String message, Data data) { this.code = code; this.message = message; this.data = data; }
        public static class Data {
            public List<Map<String, Object>> overspendings;
            public Data(List<Map<String, Object>> overspendings) { this.overspendings = overspendings; }
        }
    }
    // ... (기존 CommunityController의 static DTO들도 여기에 추가) ...
} 