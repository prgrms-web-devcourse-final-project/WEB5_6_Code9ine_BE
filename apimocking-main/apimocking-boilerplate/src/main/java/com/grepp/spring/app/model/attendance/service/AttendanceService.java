package com.grepp.spring.app.model.attendance.service;

import com.grepp.spring.app.model.attendance.domain.Attendance;
import com.grepp.spring.app.model.attendance.model.AttendanceDTO;
import com.grepp.spring.app.model.attendance.repos.AttendanceRepository;
import com.grepp.spring.app.model.attendance_summary.domain.AttendanceSummary;
import com.grepp.spring.app.model.attendance_summary.repos.AttendanceSummaryRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.util.NotFoundException;
import com.grepp.spring.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;
    private final AttendanceSummaryRepository attendanceSummaryRepository;

    public AttendanceService(final AttendanceRepository attendanceRepository,
            final MemberRepository memberRepository,
            final AttendanceSummaryRepository attendanceSummaryRepository) {
        this.attendanceRepository = attendanceRepository;
        this.memberRepository = memberRepository;
        this.attendanceSummaryRepository = attendanceSummaryRepository;
    }

    public List<AttendanceDTO> findAll() {
        final List<Attendance> attendances = attendanceRepository.findAll(Sort.by("attendanceId"));
        return attendances.stream()
                .map(attendance -> mapToDTO(attendance, new AttendanceDTO()))
                .toList();
    }

    public AttendanceDTO get(final Long attendanceId) {
        return attendanceRepository.findById(attendanceId)
                .map(attendance -> mapToDTO(attendance, new AttendanceDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final AttendanceDTO attendanceDTO) {
        final Attendance attendance = new Attendance();
        mapToEntity(attendanceDTO, attendance);
        return attendanceRepository.save(attendance).getAttendanceId();
    }

    public void update(final Long attendanceId, final AttendanceDTO attendanceDTO) {
        final Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(attendanceDTO, attendance);
        attendanceRepository.save(attendance);
    }

    public void delete(final Long attendanceId) {
        attendanceRepository.deleteById(attendanceId);
    }

    private AttendanceDTO mapToDTO(final Attendance attendance, final AttendanceDTO attendanceDTO) {
        attendanceDTO.setAttendanceId(attendance.getAttendanceId());
        attendanceDTO.setDate(attendance.getDate());
        attendanceDTO.setIsAttended(attendance.getIsAttended());
        attendanceDTO.setMember(attendance.getMember() == null ? null : attendance.getMember().getMemberId());
        return attendanceDTO;
    }

    private Attendance mapToEntity(final AttendanceDTO attendanceDTO, final Attendance attendance) {
        attendance.setDate(attendanceDTO.getDate());
        attendance.setIsAttended(attendanceDTO.getIsAttended());
        final Member member = attendanceDTO.getMember() == null ? null : memberRepository.findById(attendanceDTO.getMember())
                .orElseThrow(() -> new NotFoundException("member not found"));
        attendance.setMember(member);
        return attendance;
    }

    public ReferencedWarning getReferencedWarning(final Long attendanceId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(NotFoundException::new);
        final AttendanceSummary attendanceAttendanceSummary = attendanceSummaryRepository.findFirstByAttendance(attendance);
        if (attendanceAttendanceSummary != null) {
            referencedWarning.setKey("attendance.attendanceSummary.attendance.referenced");
            referencedWarning.addParam(attendanceAttendanceSummary.getASId());
            return referencedWarning;
        }
        return null;
    }

}
