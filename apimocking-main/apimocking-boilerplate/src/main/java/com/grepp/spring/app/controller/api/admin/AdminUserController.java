package com.grepp.spring.app.controller.api.admin;

import com.grepp.spring.app.model.admin.dto.AdminUserResponse;
import com.grepp.spring.app.model.admin.service.AdminUserService;
import com.grepp.spring.infra.payload.PageParam;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin-users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Profile("!mock")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "관리자 모든 유저 조회")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers(
        @ParameterObject PageParam pageParam
    ) {
        List<AdminUserResponse> result = adminUserService.getAllUsers(pageParam);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(result));
    }

    @GetMapping("/search")
    @Operation(summary = "관리자 유저 닉네임으로 검색")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUserByNickname(
        @RequestParam String nickname
    ) {
        AdminUserResponse result = adminUserService.getUserByNickname(nickname);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(result));
    }

    @PatchMapping("/{member-id}/block")
    @Operation(summary = "관리자 유저 차단")
    public ResponseEntity<ApiResponse<Map<String, String>>> blockUser(
        @PathVariable("member-id") Long id
    ) {
        adminUserService.blockUser(id);

        Map<String, String> response = Map.of("message", "유저가 차단되었습니다.");

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

}
