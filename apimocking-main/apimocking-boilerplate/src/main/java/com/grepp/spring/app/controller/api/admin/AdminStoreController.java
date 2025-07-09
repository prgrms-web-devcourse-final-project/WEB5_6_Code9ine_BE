package com.grepp.spring.app.controller.api.admin;

import com.grepp.spring.app.model.admin.dto.AdminStoreCreateRequest;
import com.grepp.spring.app.model.admin.dto.AdminStoreMenuResponse;
import com.grepp.spring.app.model.admin.dto.AdminStoreResponse;
import com.grepp.spring.app.model.admin.dto.AdminStoreUpdateRequest;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/admin-stores", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminStoreController {

    List<AdminStoreResponse> mockStores = List.of(
        new AdminStoreResponse(
            0,
            "대박 대패삼겹&우삼겹",
            "서울 송파구 천호대로152길 11-1",
            "한식",
            List.of(
                new AdminStoreMenuResponse("삼겹살(100g)", 4500),
                new AdminStoreMenuResponse("우삼겹(100g)", 4500),
                new AdminStoreMenuResponse("대패삼겹살(100g)", 4500)
            )
        ),

        new AdminStoreResponse(
            1,
            "고덕옛날손짜장",
            "서울특별시 서울 강동구 동남로85길 36, 1층",
            "중식",
            List.of(
                new AdminStoreMenuResponse("대패삼겹살", 4500),
                new AdminStoreMenuResponse("우삼겹", 4500)
            )
        ),

        new AdminStoreResponse(
            2,
            "하늘정원 수제왕돈까스",
            "서울특별시 서대문구 거북골로 53-6, 1층(남가좌동)",
            "일식",
            List.of(
                new AdminStoreMenuResponse("기계짜장", 5000)
            )
        )
    );

    @GetMapping
    @Operation(summary = "관리자 모든 장소 조회")
    public ResponseEntity<ApiResponse<List<AdminStoreResponse>>> getAllStores() {
        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(mockStores));
    }

    @GetMapping("/search")
    @Operation(summary = "관리자 카테고리별 장소 조회")
    public ResponseEntity<ApiResponse<List<AdminStoreResponse>>> searchByCategory(@RequestParam String category) {
        if (!List.of("한식", "중식", "일식", "양식", "미용업", "세탁업", "숙박업").contains(category)) {
            return ResponseEntity
                .status(ResponseCode.BAD_REQUEST.status())
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
        }

        List<AdminStoreResponse> result = mockStores.stream()
            .filter(p -> p.category().equals(category))
            .toList();

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(result));
    }

    @PostMapping
    @Operation(summary = "관리자 장소 등록")
    public ResponseEntity<ApiResponse<Map<String, String>>> createStore(@RequestBody @Valid AdminStoreCreateRequest request) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "장소 등록이 완료되었습니다");

        return ResponseEntity
            .status(ResponseCode.CREATED.status())
            .body(ApiResponse.successToCreate(response));
    }

    @PatchMapping("/{store-id}")
    @Operation(summary = "관리자 장소 수정")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateStore(
        @PathVariable("store-id") int id,
        @RequestBody AdminStoreUpdateRequest request) {
        if(id != 0 && id != 1 && id !=2){
            return ResponseEntity
                .status(ResponseCode.NOT_FOUND.status())
                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "장소가 수정되었습니다.");

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }

    @PatchMapping("/{store-id}/delete")
    @Operation(summary = "관리자 장소 삭제")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteStore(@PathVariable("store-id") int id) {

        if(id != 0 && id != 1 && id !=2){
            return ResponseEntity
                .status(ResponseCode.NOT_FOUND.status())
                .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "장소가 삭제되었습니다.");

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(response));
    }
}
