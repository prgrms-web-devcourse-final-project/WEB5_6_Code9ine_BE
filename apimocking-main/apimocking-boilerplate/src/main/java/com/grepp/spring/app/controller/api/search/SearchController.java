package com.grepp.spring.app.controller.api.search;

import com.grepp.spring.app.model.store.dto.PlaceResponse;
import com.grepp.spring.app.model.store.dto.RegionResponse;
import com.grepp.spring.app.model.store.service.StoreSearchService;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
@Slf4j
public class SearchController {

    private final StoreSearchService storeSearchService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PlaceResponse>>> searchPlace(
            @RequestParam String location,
            @RequestParam (required = false) List<String> type,
            @RequestParam (required = false) List<String> category
    ) {
        log.info("검색 요청 들어옴: location={}, type={}, category={}", location, type, category);
        if (location == null || location.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ResponseCode.BAD_REQUEST));
        }

        // null-safe 처리
        type = type == null ? new ArrayList<>() : new ArrayList<>(type);
        category = category == null ? new ArrayList<>() : new ArrayList<>(category);

        // 카테고리가 존재하면 store 포함
        if (!category.isEmpty() && !type.contains("store")) {
            type.add("store");
        }

        // type, category 모두 없으면 전체조회

        if (type.isEmpty() && category.isEmpty()) {
            type.addAll(List.of("store", "festival", "library"));
        }

        try{
            List<PlaceResponse> result = storeSearchService.search(location, type, category);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }
    }

    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<RegionResponse>>> getTopKeywords() {
        try {
            List<RegionResponse> keywords = storeSearchService.getTopKeywords();
            return ResponseEntity.ok(ApiResponse.success(keywords));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }
    }
}
