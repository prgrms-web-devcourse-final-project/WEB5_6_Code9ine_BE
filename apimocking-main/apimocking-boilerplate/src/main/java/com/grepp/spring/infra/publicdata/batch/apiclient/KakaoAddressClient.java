package com.grepp.spring.infra.publicdata.batch.apiclient;

import com.grepp.spring.infra.publicdata.batch.dto.CoordDto;
import com.grepp.spring.infra.publicdata.batch.dto.KakaoAddressResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!mock")

public class KakaoAddressClient {

    // 카카오 api 키
    @Value("${kakao.rest-api-key}")
    private String kakaoApiKey;

    // 의존성 주입
    private WebClient webClient;

    // 카카오 api 요청 url
    @PostConstruct
    public void initWebClient() {
        log.info("kakao api key 확인! : {}", kakaoApiKey);
        this.webClient = WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoApiKey)
                .build();
    }

    // 주소를 담아 요청 보내기
    public Optional<CoordDto> getCoordinates(String address) {
        if (address == null || address.isBlank()) {
            log.info("주소가 비어있습니다.");
            return Optional.empty();
        }

        String cleaned = address.trim();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/address.json")
                        .queryParam("query", cleaned)
                        .build())
                .retrieve()
                .bodyToMono(KakaoAddressResponse.class)
                .mapNotNull(response -> {
                    if (response.getDocuments() == null || response.getDocuments().isEmpty()) {
                        log.info("좌표 변환 실패: '{}'", cleaned);
                        return null;
                    }
                    KakaoAddressResponse.Document doc = response.getDocuments().get(0);
                    return new CoordDto(Double.parseDouble(doc.getY()), Double.parseDouble(doc.getX()));
                })
                .onErrorResume(e -> {
                    log.info("카카오 API 호출 실패 (주소: {}): {}", cleaned, e.getMessage());
                    return Mono.empty();
                })
                .blockOptional();
    }
}