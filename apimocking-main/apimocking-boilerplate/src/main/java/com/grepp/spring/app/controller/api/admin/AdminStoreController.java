package com.grepp.spring.app.controller.api.admin;

import com.grepp.spring.app.model.admin.dto.AdminStoreCreateRequest;
import com.grepp.spring.app.model.admin.dto.AdminStoreResponse;
import com.grepp.spring.app.model.admin.service.AdminStoreService;
import com.grepp.spring.infra.error.exceptions.BadRequestException;
import com.grepp.spring.infra.payload.PageParam;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/search")
    @Operation(summary = "관리자 지정 카테고리로 가게 조회", description = "카테고리 : 한식, 중식, 일식, 양식, 미용업, 세탁업, 숙박업")
    public ResponseEntity<ApiResponse<List<AdminStoreResponse>>> getStoreByCategory(
        @RequestParam String category,
        @ParameterObject PageParam pageParam
    ) {
        List<String> categories = List.of("한식", "중식", "일식", "양식", "미용업", "세탁업", "숙박업");

        if (!categories.contains(category)) {
            throw new BadRequestException("유효하지 않은 카테고리입니다.");
        }

        List<AdminStoreResponse> result = adminStoreService.getStoreByCategory(category, pageParam);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(result));
    }

    @PostMapping
    @Operation(summary = "관리자 장소 등록")
    public ResponseEntity<ApiResponse<Map<String, String>>> createStore(
        @RequestBody @Valid AdminStoreCreateRequest request
    ) {
        adminStoreService.createStore(request);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(Map.of("message", "장소 등록이 완료되었습니다.")));
    }

}
