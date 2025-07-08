package com.grepp.spring.app.controller.api.searchPlaceController;

import com.grepp.spring.infra.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/places")
public class searchPlaceController {

    // 식당검색 (location만 검색시 강남구에 해당하는것, type을 넣어 카테고리분류)
    @GetMapping("/search")
    public ApiResponse<List<Map<String, Object>>> searchMockPlaces(
            @RequestParam String location,
            @RequestParam(required = false) String type
    ) {
        List<Map<String, Object>> results = new ArrayList<>();

        if (!"강남구".equals(location)) {
            return ApiResponse.success(results); // location이 "강남구"가 아니면 빈 리스트 반환
        }

        if (type == null || type.isBlank()) {
            results.addAll(mockStores());
            results.addAll(mockFestivals());
            results.addAll(mockLibraries());
        } else {
            switch (type.toLowerCase()) {
                case "korean":
                case "chinese":
                case "western":
                    results.addAll(mockStores().stream()
                            .filter(store -> type.equals(store.get("category")))
                            .toList());
                    break;
                case "festival":
                    results.addAll(mockFestivals());
                    break;
                case "library":
                    results.addAll(mockLibraries());
                    break;
            }
        }

        return ApiResponse.success(results);
    }

    private List<Map<String, Object>> mockStores() {
        List<Map<String, Object>> stores = new ArrayList<>();

        Map<String, Object> korean = new HashMap<>();
        korean.put("name", "강남 한식당");
        korean.put("address", "서울 강남구 테헤란로 10");
        korean.put("category", "korean");
        korean.put("firstmenu", "비빔밥");
        korean.put("firstprice", 8000);
        korean.put("latitude", 37.501);
        korean.put("longitude", 127.039);
        stores.add(korean);

        Map<String, Object> chinese = new HashMap<>();
        chinese.put("name", "강남 중식당");
        chinese.put("address", "서울 강남구 역삼로 20");
        chinese.put("category", "chinese");
        chinese.put("firstmenu", "짜장면");
        chinese.put("firstprice", 7000);
        chinese.put("latitude", 37.495);
        chinese.put("longitude", 127.030);
        stores.add(chinese);

        Map<String, Object> western = new HashMap<>();
        western.put("name", "강남 양식당");
        western.put("address", "서울 강남구 봉은사로 30");
        western.put("category", "western");
        western.put("firstmenu", "파스타");
        western.put("firstprice", 12000);
        western.put("latitude", 37.504);
        western.put("longitude", 127.037);
        stores.add(western);

        return stores;
    }

    private List<Map<String, Object>> mockFestivals() {
        Map<String, Object> festival = new HashMap<>();
        festival.put("name", "강남 문화축제");
        festival.put("address", "서울 강남구 삼성로 40");
        festival.put("url","https://example.com");
        festival.put("category", null);
        festival.put("firstmenu", null);
        festival.put("firstprice", null);
        festival.put("latitude", 37.510);
        festival.put("longitude", 127.056);

        return List.of(festival);
    }

    private List<Map<String, Object>> mockLibraries() {
        Map<String, Object> library = new HashMap<>();
        library.put("name", "강남 도서관");
        library.put("address", "서울 강남구 논현로 50");
        library.put("url","https://example.com");
        library.put("category", null);
        library.put("firstmenu", null);
        library.put("firstprice", null);
        library.put("latitude", 37.512);
        library.put("longitude", 127.041);

        return List.of(library);
    }

}