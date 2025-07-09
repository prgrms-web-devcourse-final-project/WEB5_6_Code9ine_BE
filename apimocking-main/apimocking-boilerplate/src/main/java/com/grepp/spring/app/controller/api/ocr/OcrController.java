package com.grepp.spring.app.controller.api.ocr;

import com.grepp.spring.infra.response.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget")
public class OcrController {

    @PostMapping("/receipt")
    public ApiResponse<Map<String, Object>> analyzeReceipt()
    {
        Map<String, Object> data = Map.of(
            "storeName", "이마트24 신촌점",
            "date", "2025-07-02",
            "items", List.of(
                Map.of("name", "삼다수 2L", "price", 1200),
                Map.of("name", "오징어땅콩", "price", 1500),
                Map.of("name", "초코우유", "price", 1300)
            ),
            "totalprice", 4000
        );
        return ApiResponse.success(data);
    }

}
