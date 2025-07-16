package com.grepp.spring.infra.oauth.kakao;

import com.grepp.spring.infra.oauth.kakao.dto.KakaoTokenResponse;
import com.grepp.spring.infra.oauth.kakao.dto.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
public class KakaoOAuthClient {

    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final WebClient webClient;

    public KakaoOAuthClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    // 카카오 액세스 토큰 발급
    public KakaoTokenResponse getAccessToken(String authorizationCode) {
        return webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(Map.of(
                        "grant_type", "authorization_code",
                        "client_id", kakaoRestApiKey,
                        "redirect_uri", redirectUri,
                        "code", authorizationCode
                ))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .doOnError(error -> log.error("카카오 토큰 발급 실패: {}", error.getMessage()))
                .block();
    }

    // 카카오 사용자 정보 조회
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        return webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(KakaoUserInfoResponse.class)
                .doOnError(error -> log.error("카카오 사용자 정보 조회 실패: {}", error.getMessage()))
                .block();
    }
} 