package com.grepp.spring.infra.publicdata.batch.apiclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.spring.infra.publicdata.batch.dto.StoreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!mock")
public class StoreApiClient {

    private final ObjectMapper objectMapper;

    @Value("${external.api.url}")
    private String apiUrl;

    @Value("${external.api.key}")
    private String apiKey;


    public List<StoreDto> fetchFilteredStores() {
        int perPage = 1000; // 한번에 가져올 데이터 수
        int totalCount = getTotalCount(perPage); // 전체 데이터 개수
        log.info("전체 데이터 수: {}", totalCount);
        int totalPages = (int) Math.ceil((double) totalCount / perPage); // 총페이지 수를 계산

        List<StoreDto> allResults = new ArrayList<>();

        // url 생성
        for (int page = 1; page <= totalPages; page++) {
            String fullUrl = apiUrl + "?page=" + page + "&perPage=" + perPage +
                    "&returnType=JSON&serviceKey=" + apiKey;
            log.info("호출 URL: {}", fullUrl);

            // API 연결 및 응답 수신
            try {
                URL url = new URL(fullUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                log.info("api 연결 및 응답");

                try (BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

                    String response = bufferedReader.lines().collect(Collectors.joining());
                    JsonNode root = objectMapper.readTree(response);
                    JsonNode dataArray = root.path("data");
                    log.info("json 파싱");

                    if (dataArray.isArray()) {
                        for (JsonNode node : dataArray) {
                            StoreDto dto = objectMapper.treeToValue(node, StoreDto.class);
                            log.debug("수신 DTO: 업소명={}, 시도={}, 업종={}", dto.get업소명(), dto.get시도(), dto.get업종());

                            boolean isSeoul = "서울특별시".equals(dto.get시도());

                            boolean isAllowedCategory = Optional.ofNullable(dto.get업종())
                                    .map(category -> category.contains("한식") || category.contains("중식") || category.contains("일식")
                                            || category.contains("양식") || category.contains("미용업")
                                            || category.contains("세탁업") || category.contains("숙박업"))
                                    .orElse(false);

                            if (isSeoul && isAllowedCategory) {
                                log.info("통과: {} / {}", dto.get업종(), dto.get시도());
                                allResults.add(dto);
                            }
                        }
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException("API 요청 실패 (page " + page + ")", e);
            }
        }
        log.info("최종 저장 대상 개수: {}", allResults.size());
        return allResults;
    }

    // 총 데이터 수를 얻기위한 메서드
    private int getTotalCount(int perPage) {
        try {
            String fullUrl = apiUrl + "?page=1&perPage=" + perPage +
                    "&returnType=JSON&serviceKey=" + apiKey;

            URL url = new URL(fullUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                String response = br.lines().collect(Collectors.joining());
                JsonNode root = objectMapper.readTree(response);

                return root.path("totalCount").asInt();
            }

        } catch (Exception e) {
            throw new RuntimeException("totalCount 조회 실패", e);
        }
    }
}
