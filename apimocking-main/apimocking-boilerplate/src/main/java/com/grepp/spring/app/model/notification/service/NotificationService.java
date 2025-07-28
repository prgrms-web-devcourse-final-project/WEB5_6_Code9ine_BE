package com.grepp.spring.app.model.notification.service;

import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.notification.domain.Notification;
import com.grepp.spring.app.model.notification.model.NotificationDTO;
import com.grepp.spring.app.model.notification.repos.NotificationRepository;
import com.grepp.spring.util.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.grepp.spring.app.controller.api.notification.NotificationController.LikeNotificationResponse;
import com.grepp.spring.app.controller.api.notification.NotificationController.CommentNotificationResponse;
import com.grepp.spring.app.controller.api.notification.NotificationController.TitleNotificationResponse;


@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    public NotificationService(final NotificationRepository notificationRepository,
            final MemberRepository memberRepository) {
        this.notificationRepository = notificationRepository;
        this.memberRepository = memberRepository;
    }

    public List<NotificationDTO> findAll() {
        final List<Notification> notifications = notificationRepository.findAll(Sort.by("notificationId"));
        return notifications.stream()
                .map(notification -> mapToDTO(notification, new NotificationDTO()))
                .toList();
    }

    public NotificationDTO get(final Long notificationId) {
        return notificationRepository.findById(notificationId)
                .map(notification -> mapToDTO(notification, new NotificationDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final NotificationDTO notificationDTO) {
        final Notification notification = new Notification();
        mapToEntity(notificationDTO, notification);
        return notificationRepository.save(notification).getNotificationId();
    }

    public void update(final Long notificationId, final NotificationDTO notificationDTO) {
        final Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(notificationDTO, notification);
        notificationRepository.save(notification);
    }

    public void delete(final Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    private NotificationDTO mapToDTO(final Notification notification,
            final NotificationDTO notificationDTO) {
        notificationDTO.setNotificationId(notification.getNotificationId());
        notificationDTO.setMessage(notification.getMessage());
        notificationDTO.setIsRead(notification.getIsRead());
        notificationDTO.setCreatedAt(notification.getCreatedAt());
        notificationDTO.setModifiedAt(notification.getModifiedAt());
        notificationDTO.setActivated(notification.getActivated());
        notificationDTO.setMember(notification.getMember() == null ? null : notification.getMember().getMemberId());
        return notificationDTO;
    }

    private Notification mapToEntity(final NotificationDTO notificationDTO,
            final Notification notification) {
        notification.setMessage(notificationDTO.getMessage());
        notification.setIsRead(notificationDTO.getIsRead());
        notification.setCreatedAt(notificationDTO.getCreatedAt());
        notification.setModifiedAt(notificationDTO.getModifiedAt());
        notification.setActivated(notificationDTO.getActivated());
        final Member member = notificationDTO.getMember() == null ? null : memberRepository.findById(notificationDTO.getMember())
                .orElseThrow(() -> new NotFoundException("member not found"));
        notification.setMember(member);
        return notification;
    }

    // 좋아요 알림 조회 (읽지 않은 알림만)
    public List<LikeNotificationResponse> getLikeNotifications(Long memberId) {
        List<Notification> notifications = notificationRepository.findByMemberMemberIdAndTypeAndIsReadFalseAndActivatedTrue(memberId, "LIKE");
        
        return notifications.stream()
                .map(notification -> new LikeNotificationResponse(
                        notification.getNotificationId(),
                        notification.getMessage(),
                        notification.getIsRead(),
                        notification.getSenderId(),
                        notification.getType()
                ))
                .toList();
    }

    // 댓글 알림 조회 (읽지 않은 알림만)
    public List<CommentNotificationResponse> getCommentNotifications(Long memberId) {
        List<Notification> notifications = notificationRepository.findByMemberMemberIdAndTypeAndIsReadFalseAndActivatedTrue(memberId, "COMMENT");
        
        return notifications.stream()
                .map(notification -> new CommentNotificationResponse(
                        notification.getNotificationId(),
                        notification.getMessage(),
                        notification.getIsRead(),
                        notification.getSenderId(),
                        notification.getType()
                ))
                .toList();
    }

    // 칭호 획득 알림 조회 (읽지 않은 알림만)
    public List<TitleNotificationResponse> getTitleNotifications(Long memberId) {
        List<Notification> notifications = notificationRepository.findByMemberMemberIdAndTypeAndIsReadFalseAndActivatedTrue(memberId, "TITLE");
        
        return notifications.stream()
                .map(notification -> new TitleNotificationResponse(
                        notification.getNotificationId(),
                        notification.getMessage(),
                        notification.getIsRead(),
                        notification.getType(),
                        notification.getSenderId() // senderId를 aTId로 활용
                ))
                .toList();
    }

    // 알림 생성 요청 DTO
    public static class NotificationCreateRequest {
        private final Long receiverId;
        private final Long senderId;
        private final String type;
        private final String customMessage;
        private final String senderName;
        private final String title;

        public NotificationCreateRequest(Long receiverId, Long senderId, String type, 
                                       String customMessage, String senderName, String title) {
            this.receiverId = receiverId;
            this.senderId = senderId;
            this.type = type;
            this.customMessage = customMessage;
            this.senderName = senderName;
            this.title = title;
        }

        // 빌더 패턴을 위한 정적 메서드들
        public static NotificationCreateRequest of(Long receiverId, Long senderId, String type) {
            return new NotificationCreateRequest(receiverId, senderId, type, null, null, null);
        }

        public static NotificationCreateRequest of(Long receiverId, Long senderId, String type, String customMessage) {
            return new NotificationCreateRequest(receiverId, senderId, type, customMessage, null, null);
        }

        public static NotificationCreateRequest of(Long receiverId, Long senderId, String type, String customMessage, String senderName) {
            return new NotificationCreateRequest(receiverId, senderId, type, customMessage, senderName, null);
        }

        public static NotificationCreateRequest of(Long receiverId, Long senderId, String type, String customMessage, String senderName, String title) {
            return new NotificationCreateRequest(receiverId, senderId, type, customMessage, senderName, title);
        }

        // Getter 메서드들
        public Long getReceiverId() { return receiverId; }
        public Long getSenderId() { return senderId; }
        public String getType() { return type; }
        public String getCustomMessage() { return customMessage; }
        public String getSenderName() { return senderName; }
        public String getTitle() { return title; }
    }

    // 알림 생성 (팀원들이 호출할 메서드)
    public Long createNotification(NotificationCreateRequest request) {
        if (request.getSenderId() != null && request.getSenderId().equals(request.getReceiverId())) {
            // 자기 자신에게는 알림 생성하지 않음
            return null;
        }
        Member receiver = memberRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new NotFoundException("수신자를 찾을 수 없습니다."));
        
        // 메시지 생성 로직
        String message = request.getCustomMessage();
        if (message == null || message.isBlank()) {
            // senderName이 없으면 senderId로 조회
            String finalSenderName = request.getSenderName();
            if ((finalSenderName == null || finalSenderName.isBlank()) && request.getSenderId() != null) {
                finalSenderName = memberRepository.findById(request.getSenderId())
                        .map(Member::getNickname)
                        .orElse("알 수 없음");
            }
            
            // type에 따라 동적으로 메시지 생성
            switch (request.getType().toUpperCase()) {
                case "LIKE" -> message = String.format("%s님이 회원님의 게시글에 좋아요를 눌렀어요!", finalSenderName != null ? finalSenderName : "알 수 없음");
                case "COMMENT" -> message = String.format("%s님이 회원님의 게시글에 댓글을 달았어요!", finalSenderName != null ? finalSenderName : "알 수 없음");
                case "TITLE" -> message = String.format("%s 칭호를 획득했어요!", request.getTitle() != null ? request.getTitle() : "칭호");
                default -> message = "새로운 알림이 도착했습니다.";
            }
        }
        
        Notification notification = new Notification();
        notification.setMessage(message); // 메시지 설정
        notification.setIsRead(false);
        notification.setSenderId(request.getSenderId());
        notification.setType(request.getType());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setModifiedAt(LocalDateTime.now());
        notification.setActivated(true);
        notification.setMember(receiver);
        
        return notificationRepository.save(notification).getNotificationId();
    }

    // 기존 메서드 호환성을 위한 오버로드
    public Long createNotification(Long receiverId, Long senderId, String type) {
        return createNotification(NotificationCreateRequest.of(receiverId, senderId, type));
    }

    // 알림 읽음 처리
    public void markNotificationAsRead(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("알림을 찾을 수 없습니다."));
        
        // 본인의 알림만 읽음 처리 가능
        if (!notification.getMember().getMemberId().equals(memberId)) {
            throw new NotFoundException("해당 알림에 대한 권한이 없습니다.");
        }
        
        notification.setIsRead(true);
        notification.setModifiedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    // 특정 타입의 모든 알림 읽음 처리
    public void markAllNotificationsAsReadByType(Long memberId, String type) {
        int updatedCount = notificationRepository.markAllNotificationsAsReadByType(
            memberId, type, LocalDateTime.now());
        
        if (updatedCount == 0) {
            throw new NotFoundException("해당 타입의 읽지 않은 알림이 없습니다.");
        }
    }

}
