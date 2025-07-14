package com.grepp.spring.app.controller.api.mock.ocr;

import com.grepp.spring.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget")
public class OcrController {

    @Operation(summary = "영수증 분석")
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
