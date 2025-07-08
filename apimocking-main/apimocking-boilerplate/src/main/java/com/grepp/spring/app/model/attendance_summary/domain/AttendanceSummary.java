package com.grepp.spring.app.model.attendance_summary.domain;

import com.grepp.spring.app.model.attendance.domain.Attendance;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class AttendanceSummary {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long aSId;


    @Column
    private Long memberId;

    @Column
    private Integer totalAttendance;

    @Column
    private Integer consecutive;

    @Column
    private Integer lastAttendance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendanceId", nullable = false)
    private Attendance attendance;

}
