package com.grepp.spring.app.model.notification.repos;

import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.notification.domain.Notification;
import feign.Param;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Notification findFirstByMember(Member member);

    // 멤버 ID, 타입, 활성화 상태로 알림 조회
    List<Notification> findByMemberMemberIdAndTypeAndActivatedTrue(Long memberId, String type);

    // 읽지 않은 알림만 조회 (멤버 ID, 타입, 활성화 상태)
    List<Notification> findByMemberMemberIdAndTypeAndIsReadFalseAndActivatedTrue(Long memberId, String type);

    // 특정 타입의 모든 알림을 읽음 처리
    @Query("UPDATE Notification n SET n.isRead = true, n.modifiedAt = :modifiedAt " +
           "WHERE n.member.memberId = :memberId AND n.type = :type AND n.isRead = false AND n.activated = true")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    int markAllNotificationsAsReadByType(
        @Param("memberId") Long memberId,
        @Param("type") String type,
        @Param("modifiedAt") LocalDateTime modifiedAt);
    
    // 모든 알림을 읽음 처리
    @Query("UPDATE Notification n SET n.isRead = true, n.modifiedAt = :modifiedAt " +
           "WHERE n.member.memberId = :memberId AND n.isRead = false AND n.activated = true")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    int markAllNotificationsAsRead(
        @Param("memberId") Long memberId,
        @Param("modifiedAt") LocalDateTime modifiedAt);

    @Query("SELECT COUNT(n) > 0 FROM Notification n " +
        "WHERE n.member.memberId = :memberId " +
        "AND n.createdAt >= :start " +
        "AND n.createdAt < :end " +
        "AND n.message = :message")
    boolean existsMonthlyNotification(
        @Param("member") Long memberId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("message") String message);

}
