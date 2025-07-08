package com.grepp.spring.app.model.attendance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AttendanceDTO {

    private Long attendanceId;

    @NotNull
    private Long memberId;

    @NotNull
    private LocalDate date;

    @NotNull
    @JsonProperty("isAttended")
    private Boolean isAttended;

    @NotNull
    private Long member;

}
