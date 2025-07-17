package com.grepp.spring.app.controller.api.notification;

import com.grepp.spring.app.model.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import com.grepp.spring.app.model.auth.domain.Principal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.grepp.spring.app.model.member.repos.MemberRepository;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "알림 API", description = "알림 관련 API")
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public NotificationController(NotificationService notificationService, JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository) {
        this.notificationService = notificationService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberRepository = memberRepository;
    }

    // DTO 정의
    public static class LikeNotificationResponse {
        public Long notificationId;
        public String message;
        public boolean read;
        public Long senderId;
        public String type;
        public LikeNotificationResponse(Long notificationId, String message, boolean read, Long senderId, String type) {
            this.notificationId = notificationId;
            this.message = message;
            this.read = read;
            this.senderId = senderId;
            this.type = type;
        }
    }
    public static class CommentNotificationResponse {
        public Long notificationId;
        public String message;
        public boolean read;
        public Long senderId;
        public String type;
        public CommentNotificationResponse(Long notificationId, String message, boolean read, Long senderId, String type) {
            this.notificationId = notificationId;
            this.message = message;
            this.read = read;
            this.senderId = senderId;
            this.type = type;
        }
    }
    public static class TitleNotificationResponse {
        public Long notificationId;
        public String message;
        public boolean read;
        public String type;
        public TitleNotificationResponse(Long notificationId, String message, boolean read, String type) {
            this.notificationId = notificationId;
            this.message = message;
            this.read = read;
            this.type = type;
        }
    }
    public static class CreateNotificationRequest {
        @NotNull public Long receiverId;
        public String message;
        @NotNull public Long senderId;
        @NotNull public String type;
        public String senderName; // 동적 메시지 생성을 위한 필드
        public String title;      // 칭호명 등 동적 메시지 생성을 위한 필드
    }
    public static class ApiResponse<T> {
        public String code;
        public String message;
        public T data;
        public ApiResponse(String code, String message, T data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
    }

    // 알림 생성
    @PostMapping
    @Operation(summary = "알림 생성", description = "알림을 생성합니다.")
    public ResponseEntity<ApiResponse<Long>> createNotification(@RequestBody CreateNotificationRequest request) {
        String message = request.message;
        String senderName = request.senderName;
        if ((senderName == null || senderName.isBlank()) && request.senderId != null) {
            memberRepository.findById(request.senderId).ifPresent(member -> {
                // senderId가 memberId와 동일하다고 가정
                request.senderName = member.getNickname();
            });
            senderName = request.senderName;
        }
        if (message == null || message.isBlank()) {
            // type에 따라 동적으로 메시지 생성
            switch (request.type.toUpperCase()) {
                case "LIKE" -> message = String.format("%s님이 회원님의 게시글에 좋아요를 눌렀어요!", senderName != null ? senderName : "알 수 없음");
                case "COMMENT" -> message = String.format("%s님이 회원님의 게시글에 댓글을 달았어요!", senderName != null ? senderName : "알 수 없음");
                case "TITLE" -> message = String.format("%s 칭호를 획득했어요!", request.title != null ? request.title : "칭호");
                default -> message = "새로운 알림이 도착했습니다.";
            }
        }
        Long id = notificationService.createNotification(request.receiverId, request.senderId, request.type);
        return ResponseEntity.status(201).body(new ApiResponse<>("2001", "성공적으로 생성되었습니다.", id));
    }

    // 커뮤니티 좋아요 알림 조회
    @GetMapping("/like")
    @Operation(summary = "커뮤니티 좋아요 알림 조회", description = "좋아요 알림 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<LikeNotificationResponse>>> getLikeNotifications(@AuthenticationPrincipal Principal principal) {
        List<LikeNotificationResponse> data = notificationService.getLikeNotifications(principal.getMemberId());
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", data));
    }

    // 커뮤니티 댓글 알림 조회
    @GetMapping("/comment")
    @Operation(summary = "커뮤니티 댓글 알림 조회", description = "댓글 알림 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CommentNotificationResponse>>> getCommentNotifications(@AuthenticationPrincipal Principal principal) {
        List<CommentNotificationResponse> data = notificationService.getCommentNotifications(principal.getMemberId());
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", data));
    }

    // 칭호 획득 알림 조회
    @GetMapping("/title")
    @Operation(summary = "칭호 획득 알림 조회", description = "칭호 알림 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<TitleNotificationResponse>>> getTitleNotifications(@AuthenticationPrincipal Principal principal) {
        List<TitleNotificationResponse> data = notificationService.getTitleNotifications(principal.getMemberId());
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", data));
    }

    // 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> markNotificationAsRead(@PathVariable Long notificationId, @AuthenticationPrincipal Principal principal) {
        notificationService.markNotificationAsRead(notificationId, principal.getMemberId());
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", null));
    }
} 