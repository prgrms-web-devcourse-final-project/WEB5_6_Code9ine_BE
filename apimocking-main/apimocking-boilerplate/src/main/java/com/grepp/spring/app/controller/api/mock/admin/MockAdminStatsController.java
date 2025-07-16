package com.grepp.spring.app.controller.api.mock.admin;

import com.grepp.spring.app.model.admin.dto.AdminStatsResponse;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("mock")
@RequestMapping(value = "/api/admin-stats", produces = MediaType.APPLICATION_JSON_VALUE)
public class MockAdminStatsController {

    @GetMapping("/daily-stats")
    @Operation(summary = "관리자 당일 통계(방문자, 회원가입 수) 조회")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getDailyStats() {
        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(
                new AdminStatsResponse(LocalDate.now().toString(), 100, 10)
            ));
    }
}
