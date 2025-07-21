package com.grepp.spring.infra.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverOcrClient {

    @Value("${naver.ocr.invoke-url}")
    private String invokeUrl;

    @Value("${naver.ocr.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String requestOcr(byte[] imageBytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-OCR-SECRET", secretKey);

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("version", "V2");
        requestMap.put("requestId", UUID.randomUUID().toString());
        requestMap.put("timestamp", System.currentTimeMillis());

        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("format", "jpg");
        imageMap.put("name", "image");
        imageMap.put("data", base64Image);

        requestMap.put("images", List.of(imageMap));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestMap, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(invokeUrl, request, String.class);
        return response.getBody();


    }


}
