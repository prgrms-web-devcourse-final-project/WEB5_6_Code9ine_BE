package com.grepp.spring.app.model.attendance_summary.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AttendanceSummaryDTO {

    @JsonProperty("aSId")
    private Long aSId;

    private Long attendanceId;

    private Long memberId;

    private Integer totalAttendance;

    private Integer consecutive;

    private Integer lastAttendance;

    @NotNull
    private Long attendance;

}
