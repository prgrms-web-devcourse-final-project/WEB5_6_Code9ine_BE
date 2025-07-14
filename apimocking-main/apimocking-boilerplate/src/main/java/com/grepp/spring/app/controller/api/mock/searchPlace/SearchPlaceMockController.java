package com.grepp.spring.app.controller.api.mock.searchPlace;

import com.grepp.spring.app.model.store.dto.BookmarkToggleRequest;
import com.grepp.spring.infra.response.ApiResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Profile("mock")
public class SearchPlaceMockController {

    @GetMapping("/places/search")
    public ApiResponse<List<Map<String, Object>>> searchMockPlaces(
            @RequestParam String location,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category
    ) {
        List<Map<String, Object>> results = new ArrayList<>();

        if (!"강남구".equals(location)) {
            return ApiResponse.success(results);
        }

        //type + category 조합 기준으로 store 자동 포함 처리
        List<String> requestedTypes = new ArrayList<>();
        if (type == null || type.isBlank()) {
            if (category == null || category.isBlank()) {
                requestedTypes = List.of("store", "festival", "library");
            } else {
                requestedTypes = List.of("store");
            }
        } else {
            requestedTypes = new ArrayList<>(List.of(type.split(",")));
            if ((category != null && !category.isBlank()) && !requestedTypes.contains("store")) {
                requestedTypes.add("store"); //핵심: store 강제 포함
            }
        }

        //store용 category
        List<String> requestedCategories = (category == null || category.isBlank())
                ? List.of()
                : List.of(category.split(","));

        if (requestedTypes.contains("store")) {
            results.addAll(mockStores().stream()
                    .filter(store -> {
                        if (requestedCategories.isEmpty()) return true;
                        Object c = store.get("category");
                        return c != null && requestedCategories.contains(c.toString());
                    })
                    .toList());
        }

        if (requestedTypes.contains("festival")) {
            results.addAll(mockFestivals());
        }

        if (requestedTypes.contains("library")) {
            results.addAll(mockLibraries());
        }

        return ApiResponse.success(results);
    }

    private List<Map<String, Object>> mockStores() {
        List<Map<String, Object>> stores = new ArrayList<>();

        Map<String, Object> korean = new HashMap<>();
        korean.put("storeId", "1");
        korean.put("name", "강남 한식당");
        korean.put("address", "서울 강남구 테헤란로 10");
        korean.put("category", "한식");
        korean.put("type", "store"); // 핵심
        korean.put("contact","02-1212-1212");
        korean.put("firstmenu", "비빔밥");
        korean.put("firstprice", 8000);
        korean.put("latitude", 37.501);
        korean.put("longitude", 127.039);
        stores.add(korean);

        Map<String, Object> chinese = new HashMap<>();
        chinese.put("storeId", "2");
        chinese.put("name", "강남 중식당");
        chinese.put("address", "서울 강남구 역삼로 20");
        chinese.put("category", "중식");
        chinese.put("type", "store");
        chinese.put("contact","02-1212-1212");
        chinese.put("firstmenu", "짜장면");
        chinese.put("firstprice", 7000);
        chinese.put("latitude", 37.495);
        chinese.put("longitude", 127.030);
        stores.add(chinese);

        Map<String, Object> western = new HashMap<>();
        western.put("storeId", "3");
        western.put("name", "강남 양식당");
        western.put("address", "서울 강남구 봉은사로 30");
        western.put("category", "양식");
        western.put("type", "store");
        western.put("contact","02-1212-1212");
        western.put("firstmenu", "파스타");
        western.put("firstprice", 12000);
        western.put("latitude", 37.504);
        western.put("longitude", 127.037);
        stores.add(western);

        return stores;
    }

    private List<Map<String, Object>> mockFestivals() {
        Map<String, Object> festival = new HashMap<>();
        festival.put("festivalId", "4");
        festival.put("name", "강남 문화축제");
        festival.put("address", "서울 강남구 삼성로 40");
        festival.put("url", "https://example.com");
        festival.put("category", null);
        festival.put("type", "festival");
        festival.put("latitude", 37.510);
        festival.put("longitude", 127.056);
        return List.of(festival);
    }

    private List<Map<String, Object>> mockLibraries() {
        Map<String, Object> library = new HashMap<>();
        library.put("libraryId", "5");
        library.put("name", "강남 도서관");
        library.put("address", "서울 강남구 논현로 50");
        library.put("url", "https://example.com");
        library.put("category", null);
        library.put("type", "library");
        library.put("contact", "02-1212-1212");
        library.put("latitude", 37.512);
        library.put("longitude", 127.041);
        return List.of(library);
    }

    @GetMapping("/places/detail")
    public ApiResponse<Map<String, Object>> getPlaceDetail(
            @RequestParam String type,
            @RequestParam Long id
    ) {
        Map<String, Object> result = new HashMap<>();

        switch (type) {
            case "store" -> {
                result.put("storeId", id);
                result.put("name", "서울식당");
                result.put("address", "서울시 강남구 테헤란로 10");
                result.put("category", "한식"); // 업종 (category)
                result.put("contact", "02-1212-1212");
                result.put("firstmenu", "비빔밥");
                result.put("firstprice", 8000);
                result.put("secondmenu", "김치찌개");
                result.put("secondprice", 7000);
                result.put("thirdmenu", "된장찌개");
                result.put("thirdprice", 7500);
                result.put("latitude", 37.501);
                result.put("longitude", 127.039);
            }
            case "festival" -> {
                result.put("festivalId", id);
                result.put("name", "강남 문화축제");
                result.put("category", "문화예술");
                result.put("address", "서울시 강남구 삼성로 40");
                result.put("target", "전체 시민");
                result.put("url", "https://festival.example.com");
                result.put("startAt", "2025-07-20");
                result.put("endAt", "2025-07-23");
                result.put("latitude", 37.51);
                result.put("longitude", 127.056);
            }
            case "library" -> {
                result.put("libraryId", id);
                result.put("name", "강남 도서관");
                result.put("address", "서울시 강남구 논현로 50");
                result.put("url", "https://library.example.com");
                result.put("contact", "02-1212-1212");
                result.put("latitude", 37.512);
                result.put("longitude", 127.041);
            }
            default -> throw new IllegalArgumentException("유효하지 않은 type 값입니다.");
        }

        return ApiResponse.success(result);
    }

    @GetMapping("/searches/top")
    public ApiResponse<List<Map<String, Object>>> getTopSearchKeywords() {
        List<Map<String, Object>> region = new ArrayList<>();

        region.add(Map.of("region", "성동구"));
        region.add(Map.of("region", "강남구"));
        region.add(Map.of("region", "마포구"));
        region.add(Map.of("region", "서초구"));
        region.add(Map.of("region", "강북구"));

        return ApiResponse.success(region);
    }

    @PatchMapping("/users/{userId}/places-bookmarks/toggle")
    public ApiResponse<Map<String, Object>> toggleBookmarkMock(
            @PathVariable Long userId,
            @RequestBody BookmarkToggleRequest request
    ) {
        Map<String, Object> response = new HashMap<>();

        if (request.getStoreId() != null) {
            response.put("activated", true);
            response.put("message", "북마크가 추가되었습니다.");
            return ApiResponse.success(response);
        }

        if (request.getFestivalId() != null) {
            response.put("activated", false);
            response.put("message", "북마크가 제거되었습니다.");
            return ApiResponse.success(response);
        }

        if (request.getLibraryId() != null) {
            response.put("activated", true);
            response.put("message", "북마크가 추가되었습니다.");
            return ApiResponse.success(response);
        }

        throw new IllegalArgumentException("storeId, festivalId, libraryId 중 하나는 반드시 포함되어야 합니다.");
    }
}

