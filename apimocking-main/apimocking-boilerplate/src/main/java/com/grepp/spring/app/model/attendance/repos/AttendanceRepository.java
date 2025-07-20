package com.grepp.spring.app.model.attendance.repos;

import com.grepp.spring.app.model.attendance.domain.Attendance;
import com.grepp.spring.app.model.member.domain.Member;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Attendance findFirstByMember(Member member);

    boolean existsByMemberAndDate(Member member, LocalDate today);
}
