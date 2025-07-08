package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.attendance_summary.model.AttendanceSummaryDTO;
import com.grepp.spring.app.model.attendance_summary.service.AttendanceSummaryService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/attendanceSummaries", produces = MediaType.APPLICATION_JSON_VALUE)
public class AttendanceSummaryResource {

    private final AttendanceSummaryService attendanceSummaryService;

    public AttendanceSummaryResource(final AttendanceSummaryService attendanceSummaryService) {
        this.attendanceSummaryService = attendanceSummaryService;
    }

    @GetMapping
    public ResponseEntity<List<AttendanceSummaryDTO>> getAllAttendanceSummaries() {
        return ResponseEntity.ok(attendanceSummaryService.findAll());
    }

    @GetMapping("/{aSId}")
    public ResponseEntity<AttendanceSummaryDTO> getAttendanceSummary(
            @PathVariable(name = "aSId") final Long aSId) {
        return ResponseEntity.ok(attendanceSummaryService.get(aSId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createAttendanceSummary(
            @RequestBody @Valid final AttendanceSummaryDTO attendanceSummaryDTO) {
        final Long createdASId = attendanceSummaryService.create(attendanceSummaryDTO);
        return new ResponseEntity<>(createdASId, HttpStatus.CREATED);
    }

    @PutMapping("/{aSId}")
    public ResponseEntity<Long> updateAttendanceSummary(
            @PathVariable(name = "aSId") final Long aSId,
            @RequestBody @Valid final AttendanceSummaryDTO attendanceSummaryDTO) {
        attendanceSummaryService.update(aSId, attendanceSummaryDTO);
        return ResponseEntity.ok(aSId);
    }

    @DeleteMapping("/{aSId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteAttendanceSummary(
            @PathVariable(name = "aSId") final Long aSId) {
        attendanceSummaryService.delete(aSId);
        return ResponseEntity.noContent().build();
    }

}
