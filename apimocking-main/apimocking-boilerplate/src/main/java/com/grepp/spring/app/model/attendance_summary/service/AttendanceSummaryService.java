package com.grepp.spring.app.model.attendance_summary.service;

import com.grepp.spring.app.model.attendance.domain.Attendance;
import com.grepp.spring.app.model.attendance.repos.AttendanceRepository;
import com.grepp.spring.app.model.attendance_summary.domain.AttendanceSummary;
import com.grepp.spring.app.model.attendance_summary.model.AttendanceSummaryDTO;
import com.grepp.spring.app.model.attendance_summary.repos.AttendanceSummaryRepository;
import com.grepp.spring.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class AttendanceSummaryService {

    private final AttendanceSummaryRepository attendanceSummaryRepository;
    private final AttendanceRepository attendanceRepository;

    public AttendanceSummaryService(final AttendanceSummaryRepository attendanceSummaryRepository,
            final AttendanceRepository attendanceRepository) {
        this.attendanceSummaryRepository = attendanceSummaryRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public List<AttendanceSummaryDTO> findAll() {
        final List<AttendanceSummary> attendanceSummaries = attendanceSummaryRepository.findAll(Sort.by("aSId"));
        return attendanceSummaries.stream()
                .map(attendanceSummary -> mapToDTO(attendanceSummary, new AttendanceSummaryDTO()))
                .toList();
    }

    public AttendanceSummaryDTO get(final Long aSId) {
        return attendanceSummaryRepository.findById(aSId)
                .map(attendanceSummary -> mapToDTO(attendanceSummary, new AttendanceSummaryDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final AttendanceSummaryDTO attendanceSummaryDTO) {
        final AttendanceSummary attendanceSummary = new AttendanceSummary();
        mapToEntity(attendanceSummaryDTO, attendanceSummary);
        return attendanceSummaryRepository.save(attendanceSummary).getASId();
    }

    public void update(final Long aSId, final AttendanceSummaryDTO attendanceSummaryDTO) {
        final AttendanceSummary attendanceSummary = attendanceSummaryRepository.findById(aSId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(attendanceSummaryDTO, attendanceSummary);
        attendanceSummaryRepository.save(attendanceSummary);
    }

    public void delete(final Long aSId) {
        attendanceSummaryRepository.deleteById(aSId);
    }

    private AttendanceSummaryDTO mapToDTO(final AttendanceSummary attendanceSummary,
            final AttendanceSummaryDTO attendanceSummaryDTO) {
        attendanceSummaryDTO.setASId(attendanceSummary.getASId());
        attendanceSummaryDTO.setMemberId(attendanceSummary.getMemberId());
        attendanceSummaryDTO.setTotalAttendance(attendanceSummary.getTotalAttendance());
        attendanceSummaryDTO.setConsecutive(attendanceSummary.getConsecutive());
        attendanceSummaryDTO.setLastAttendance(attendanceSummary.getLastAttendance());
        attendanceSummaryDTO.setAttendance(attendanceSummary.getAttendance() == null ? null : attendanceSummary.getAttendance().getAttendanceId());
        return attendanceSummaryDTO;
    }

    private AttendanceSummary mapToEntity(final AttendanceSummaryDTO attendanceSummaryDTO,
            final AttendanceSummary attendanceSummary) {
        attendanceSummary.setMemberId(attendanceSummaryDTO.getMemberId());
        attendanceSummary.setTotalAttendance(attendanceSummaryDTO.getTotalAttendance());
        attendanceSummary.setConsecutive(attendanceSummaryDTO.getConsecutive());
        attendanceSummary.setLastAttendance(attendanceSummaryDTO.getLastAttendance());
        final Attendance attendance = attendanceSummaryDTO.getAttendance() == null ? null : attendanceRepository.findById(attendanceSummaryDTO.getAttendance())
                .orElseThrow(() -> new NotFoundException("attendance not found"));
        attendanceSummary.setAttendance(attendance);
        return attendanceSummary;
    }

}
