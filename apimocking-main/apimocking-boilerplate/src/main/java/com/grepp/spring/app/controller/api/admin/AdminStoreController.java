package com.grepp.spring.app.controller.api.admin;

import com.grepp.spring.app.model.admin.dto.AdminStoreCreateRequest;
import com.grepp.spring.app.model.admin.dto.AdminStoreMenuResponse;
import com.grepp.spring.app.model.admin.dto.AdminStoreResponse;
import com.grepp.spring.app.model.admin.dto.AdminStoreUpdateRequest;
import com.grepp.spring.infra.payload.PageParam;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springdoc.core.annotations.ParameterObject;
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

    List<String> categories = List.of(
        "한식", "중식", "일식", "양식", "미용업", "세탁업", "숙박업"
    );


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
        ),

        new AdminStoreResponse(
            3,
            "하이얀미용실",
            "서울특별시 서울 양천구 은행정로5길 48-3",
            "미용업",
            List.of(
                new AdminStoreMenuResponse("커트", 6000),
                new AdminStoreMenuResponse("염색", 15000)
            )
        ),

        new AdminStoreResponse(
            4,
            "긴밀",
            "서울특별시 서울 송파구 삼전로9길 19. 1층 102호",
            "양식",
            List.of(
                new AdminStoreMenuResponse("크림파스타", 14000),
                new AdminStoreMenuResponse("오일파스타", 13000)
            )
        ),

        new AdminStoreResponse(
            5,
            "국수가",
            "서울특별시 서울 동작구 사당로16가길 18",
            "양식",
            List.of(
                new AdminStoreMenuResponse("칼국수", 7000)
            )
        ),

        new AdminStoreResponse(
            6,
            "소오",
            "서울특별시 서울 서대문구 이화여대길 50-10",
            "한식",
            List.of(
                new AdminStoreMenuResponse("제육덮밥", 85000),
                new AdminStoreMenuResponse("카레라이스", 85000)
            )
        ),

        new AdminStoreResponse(
            7,
            "짜앤짬",
            "서울특별시 서울 중랑구 겸재로 130,1층",
            "중식",
            List.of(
                new AdminStoreMenuResponse("짬뽕", 5000),
                new AdminStoreMenuResponse("자장면", 4000)
            )
        ),

        new AdminStoreResponse(
            8,
            "도깨비칼국수",
            "서울특별시 강북구 도봉로 183",
            "한식",
            List.of(
                new AdminStoreMenuResponse("비빔냉면", 6000),
                new AdminStoreMenuResponse("칼국수", 4000),
                new AdminStoreMenuResponse("물냉면", 6000)
            )
        ),

        new AdminStoreResponse(
            9,
            "칸스KANS",
            "서울특별시 서울 강서구 방화동로5길 12-1",
            "양식",
            List.of(
                new AdminStoreMenuResponse("치킨브리또", 6500),
                new AdminStoreMenuResponse("치미창가", 9500),
                new AdminStoreMenuResponse("(칸스)브리또", 4900)
            )
        ),

        new AdminStoreResponse(
            10,
            "신데렐라미용실",
            "서울특별시 서울 동작구 신대방길 95",
            "미용업",
            List.of(
                new AdminStoreMenuResponse("커트", 12000),
                new AdminStoreMenuResponse("염색", 35000)
            )
        ),

        new AdminStoreResponse(
            11,
            "대광사",
            "서울특별시 서울 동작구 상도로15길 115",
            "세탁업",
            List.of(
                new AdminStoreMenuResponse("양복1벌", 8000)
            )
        ),

        new AdminStoreResponse(
            12,
            "더샵명품세탁",
            "서울특별시 서울 동작구 장승배기로4길 9 비동 101호",
            "세탁업",
            List.of(
                new AdminStoreMenuResponse("바지", 4000)
            )
        )
    );

    @GetMapping
    @Operation(summary = "관리자 모든 장소 조회")
    public ResponseEntity<ApiResponse<List<AdminStoreResponse>>> getAllStores(
        @ParameterObject PageParam pageParam
    ) {

        int page = pageParam.getPage();
        int size = pageParam.getSize();

        int fromIndex =  (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, mockStores.size());

        if (fromIndex >= mockStores.size()) {
            return ResponseEntity
                .status(ResponseCode.BAD_REQUEST.status())
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
        }

        List<AdminStoreResponse> paged = mockStores.subList(fromIndex, toIndex);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(paged));
    }

    @GetMapping("/search")
    @Operation(summary = "관리자 카테고리별 장소 조회", description = "카테고리 : 한식, 중식, 일식, 양식, 미용업, 세탁업, 숙박업")
    public ResponseEntity<ApiResponse<List<AdminStoreResponse>>> searchByCategory(
        @RequestParam String category,
        @ParameterObject PageParam pageParam
    ) {

        if (!categories.contains(category)) {
            return ResponseEntity
                .status(ResponseCode.BAD_REQUEST.status())
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
        }

        List<AdminStoreResponse> result = mockStores.stream()
            .filter(p -> p.category().equals(category))
            .toList();

        int page = pageParam.getPage();
        int size = pageParam.getSize();

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, result.size());

        if (fromIndex >= result.size()) {
            return ResponseEntity
                .status(ResponseCode.BAD_REQUEST.status())
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
        }

        List<AdminStoreResponse> paged = result.subList(fromIndex, toIndex);

        return ResponseEntity
            .status(ResponseCode.OK.status())
            .body(ApiResponse.success(paged));
    }

//    @PostMapping
//    @Operation(summary = "관리자 장소 등록")
//    public ResponseEntity<ApiResponse<Map<String, String>>> createStore(@RequestBody @Valid AdminStoreCreateRequest request) {
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "장소 등록이 완료되었습니다");
//
//        return ResponseEntity
//            .status(ResponseCode.CREATED.status())
//            .body(ApiResponse.successToCreate(response));
//    }

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