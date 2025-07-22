package com.grepp.spring.infra.ocr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.spring.infra.ocr.dto.ReceiptDataDto;
import com.grepp.spring.infra.ocr.dto.ReceiptItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverOcrClient {

    // url ocr의 자동 API GATEWAY 생성된 url
    @Value("${naver.ocr.invoke-url}")
    private String ocrUrl;

    // GATE WAY 연동의 생성한 시크릿 키
    @Value("${naver.ocr.secret-key}")
    private String secretKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReceiptDataDto requestOcr(MultipartFile imageFile) {
        try {
            // 1. OCR 메시지 바디 구성
            Map<String, Object> messageBody = Map.of(
                    "version", "V2",
                    "requestId", UUID.randomUUID().toString(),
                    "timestamp", System.currentTimeMillis(),
                    "images", List.of(Map.of("format", "jpg", "name", "demo"))
            );

            // 2. Header 구성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("X-OCR-SECRET", secretKey);

            // 3. Body 구성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("message", objectMapper.writeValueAsString(messageBody));
            body.add("file", new ByteArrayResource(imageFile.getBytes()) {
                @Override
                public String getFilename() {
                    return imageFile.getOriginalFilename();
                }
            });

            // 4. WebClient 생성 및 요청
            WebClient webClient = WebClient.builder()
                    .baseUrl(ocrUrl)
                    .build();

            String rawResponse = webClient.post()
                    .headers(h -> h.addAll(headers))
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("[requestOcr] OCR 응답 원본: {}", rawResponse);

            // 5. JSON 파싱
            return parseOcrResponse(rawResponse);

        } catch (Exception e) {
            log.error("[requestOcr] OCR 요청 실패", e);
            throw new RuntimeException("OCR 호출 실패");
        }
    }

    private ReceiptDataDto parseOcrResponse(String rawJson) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(rawJson);

        JsonNode imageNode = root.path("images");
        if (!imageNode.isArray() || imageNode.isEmpty()) {
            throw new RuntimeException("OCR 응답에 이미지 정보가 없습니다.");
        }

        JsonNode receiptResultNode = imageNode.get(0)
                .path("receipt")
                .path("result");

        if (receiptResultNode.isMissingNode()) {
            throw new RuntimeException("OCR 응답에 영수증 분석 결과가 없습니다.");
        }

        String storeName = receiptResultNode.path("storeInfo").path("name").path("text").asText("");
        String date = receiptResultNode.path("paymentInfo").path("date").path("formatted").asText("");

        List<ReceiptItemDto> items = new ArrayList<>();
        int total = 0;

        JsonNode itemsNode = receiptResultNode.path("subResults").get(0).path("items");
        if (itemsNode != null && itemsNode.isArray()) {
            for (JsonNode item : itemsNode) {
                String name = item.path("name").path("text").asText("");

                int price = 0;
                JsonNode priceNode = item.path("price");

                if (priceNode.has("price")) {
                    JsonNode priceDetailNode = priceNode.path("price");
                    if (priceDetailNode.has("formatted") && priceDetailNode.path("formatted").has("value")) {
                        price = priceDetailNode.path("formatted").path("value").asInt(0);
                    } else if (priceDetailNode.has("text")) {
                        try {
                            price = Integer.parseInt(priceDetailNode.path("text").asText().replaceAll(",", ""));
                        } catch (NumberFormatException e) {
                            log.warn("[parseOcrResponse] 금액 파싱 실패 (text): {}", priceDetailNode.path("text").asText());
                        }
                    }
                } else if (priceNode.has("text")) {
                    try {
                        price = Integer.parseInt(priceNode.path("text").asText().replaceAll(",", ""));
                    } catch (NumberFormatException e) {
                        log.warn("[parseOcrResponse] 금액 파싱 실패 (fallback): {}", priceNode.path("text").asText());
                    }
                }

                total += price;
                items.add(new ReceiptItemDto(name, price));
            }
        }

        //총 금액은 totalPrice의 formatted.value에서 읽기
        int totalPrice = receiptResultNode
                .path("totalPrice")
                .path("price")
                .path("formatted")
                .path("value")
                .asInt(total); // 파싱 실패 시 items 합산 total로 대체

        return new ReceiptDataDto(storeName, date, items, totalPrice);
    }
}
