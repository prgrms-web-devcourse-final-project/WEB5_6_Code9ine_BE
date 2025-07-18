package com.grepp.spring.app.controller.api.admin;

import com.grepp.spring.app.model.admin.dto.AdminStatsResponse;
import com.grepp.spring.app.model.admin.service.AdminStatsService;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin-stats")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @GetMapping("/daily-stats")
    @Operation(summary = "관리자 당일 통계(방문자 수, 회원가입 수) 조회")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getTodayStats() {
        AdminStatsResponse response = adminStatsService.getTodayStats();

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

}
