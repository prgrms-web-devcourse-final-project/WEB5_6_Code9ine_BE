package com.grepp.spring.app.controller.api.admin;

import com.grepp.spring.app.model.admin.dto.AdminStoreResponse;
import com.grepp.spring.app.model.admin.dto.AdminUserResponse;
import com.grepp.spring.app.model.admin.service.AdminStoreService;
import com.grepp.spring.app.model.admin.service.AdminUserService;
import com.grepp.spring.infra.payload.PageParam;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin-stores")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Profile("!mock")
public class AdminStoreController {

    private final AdminStoreService adminStoreService;

    @GetMapping
    @Operation(summary = "관리자 모든 가게 조회")
    public ResponseEntity<ApiResponse<List<AdminStoreResponse>>> getAllStores(
        @ParameterObject PageParam pageParam
    ) {
        List<AdminStoreResponse> result = adminStoreService.getAllStores(pageParam);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(result));
    }

}
