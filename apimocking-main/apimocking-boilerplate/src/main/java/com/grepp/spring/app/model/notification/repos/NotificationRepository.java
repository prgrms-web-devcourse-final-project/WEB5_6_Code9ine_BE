package com.grepp.spring.app.model.notification.repos;

import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Notification findFirstByMember(Member member);

    // 멤버 ID, 타입, 활성화 상태로 알림 조회
    List<Notification> findByMemberMemberIdAndTypeAndActivatedTrue(Long memberId, String type);

}
