package com.grepp.spring.app.controller.api.admin;

import com.grepp.spring.app.model.admin.dto.AdminStoreCreateRequest;
import com.grepp.spring.app.model.admin.dto.AdminStoreListResponse;
import com.grepp.spring.app.model.admin.dto.AdminStoreResponse;
import com.grepp.spring.app.model.admin.dto.AdminStoreUpdateRequest;
import com.grepp.spring.app.model.admin.service.AdminStoreService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<ApiResponse<AdminStoreListResponse>> getAllStores(
        @ParameterObject PageParam pageParam
    ) {
        AdminStoreListResponse result = adminStoreService.getAllStores(pageParam);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(result));
    }

    @GetMapping("/search")
    @Operation(summary = "관리자 지정 카테고리로 가게 조회", description = "카테고리 : 한식, 중식, 일식, 양식, 미용업, 세탁업, 숙박업")
    public ResponseEntity<ApiResponse<AdminStoreListResponse>> getStoreByCategory(
        @RequestParam String category,
        @ParameterObject PageParam pageParam
    ) {
        AdminStoreListResponse result = adminStoreService.getStoresByCategory(category, pageParam);

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

    @PatchMapping("/{store-id}")
    @Operation(summary = "관리자 장소 수정", description = "카테고리 : 한식, 중식, 일식, 양식, 미용업, 세탁업, 숙박업")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateStore(
        @PathVariable("store-id") Long id,
        @RequestBody @Valid AdminStoreUpdateRequest request
    ) {
        adminStoreService.updateStore(id, request);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(Map.of("message", "장소가 수정되었습니다.")));
    }

    @PatchMapping("/{store-id}/delete")
    @Operation(summary = "관리자 장소 삭제")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteStore(
        @PathVariable("store-id") Long id
    ) {
        adminStoreService.deleteStore(id);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(Map.of("message", "장소가 삭제되었습니다.")));
    }
}
