package com.grepp.spring.app.model.attendance.repos;

import com.grepp.spring.app.model.attendance.domain.Attendance;
import com.grepp.spring.app.model.member.domain.Member;
import feign.Param;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Attendance findFirstByMember(Member member);

    boolean existsByMemberAndDate(Member member, LocalDate today);

    // 회원 탈퇴 시 해당 회원의 모든 출석 데이터 삭제
    @Query("DELETE FROM Attendance a WHERE a.member = :member")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteByMember(@Param("member") Member member);
}
