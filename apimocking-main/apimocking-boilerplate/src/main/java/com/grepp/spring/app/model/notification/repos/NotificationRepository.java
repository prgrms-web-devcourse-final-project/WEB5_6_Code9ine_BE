package com.grepp.spring.app.model.notification.repos;

import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Notification findFirstByMember(Member member);

}
