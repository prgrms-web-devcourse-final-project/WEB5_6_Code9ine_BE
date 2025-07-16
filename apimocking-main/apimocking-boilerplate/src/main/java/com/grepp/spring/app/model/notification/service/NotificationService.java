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

    // 좋아요 알림 조회
    public List<com.grepp.spring.app.controller.api.notification.NotificationController.LikeNotificationResponse> getLikeNotifications(String memberId) {
        Long memberIdLong = Long.parseLong(memberId);
        List<Notification> notifications = notificationRepository.findByMemberMemberIdAndTypeAndActivatedTrue(memberIdLong, "LIKE");
        
        return notifications.stream()
                .map(notification -> new com.grepp.spring.app.controller.api.notification.NotificationController.LikeNotificationResponse(
                        notification.getNotificationId(),
                        notification.getMessage(),
                        notification.getIsRead(),
                        notification.getSenderId(),
                        notification.getType()
                ))
                .toList();
    }

    // 댓글 알림 조회
    public List<com.grepp.spring.app.controller.api.notification.NotificationController.CommentNotificationResponse> getCommentNotifications(String memberId) {
        Long memberIdLong = Long.parseLong(memberId);
        List<Notification> notifications = notificationRepository.findByMemberMemberIdAndTypeAndActivatedTrue(memberIdLong, "COMMENT");
        
        return notifications.stream()
                .map(notification -> new com.grepp.spring.app.controller.api.notification.NotificationController.CommentNotificationResponse(
                        notification.getNotificationId(),
                        notification.getMessage(),
                        notification.getIsRead(),
                        notification.getSenderId(),
                        notification.getType()
                ))
                .toList();
    }

    // 칭호 획득 알림 조회
    public List<com.grepp.spring.app.controller.api.notification.NotificationController.TitleNotificationResponse> getTitleNotifications(String memberId) {
        Long memberIdLong = Long.parseLong(memberId);
        List<Notification> notifications = notificationRepository.findByMemberMemberIdAndTypeAndActivatedTrue(memberIdLong, "TITLE");
        
        return notifications.stream()
                .map(notification -> new com.grepp.spring.app.controller.api.notification.NotificationController.TitleNotificationResponse(
                        notification.getNotificationId(),
                        notification.getMessage(),
                        notification.getIsRead(),
                        notification.getType()
                ))
                .toList();
    }

    // 알림 생성 (팀원들이 호출할 메서드)
    public Long createNotification(Long receiverId, String message, Integer senderId, String type) {
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException("수신자를 찾을 수 없습니다."));
        
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setSenderId(senderId);
        notification.setType(type);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setModifiedAt(LocalDateTime.now());
        notification.setActivated(true);
        notification.setMember(receiver);
        
        return notificationRepository.save(notification).getNotificationId();
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

}
