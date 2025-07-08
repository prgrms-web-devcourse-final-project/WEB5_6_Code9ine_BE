package com.grepp.spring.app.model.attendance_summary.repos;

import com.grepp.spring.app.model.attendance.domain.Attendance;
import com.grepp.spring.app.model.attendance_summary.domain.AttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary, Long> {

    AttendanceSummary findFirstByAttendance(Attendance attendance);

}
