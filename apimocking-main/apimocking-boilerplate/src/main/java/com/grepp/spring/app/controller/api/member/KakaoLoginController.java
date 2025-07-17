package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.model.auth.AuthService;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.member.service.MemberService;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.infra.oauth.kakao.KakaoOAuthClient;
import com.grepp.spring.infra.oauth.kakao.dto.KakaoTokenResponse;
import com.grepp.spring.infra.oauth.kakao.dto.KakaoUserInfoResponse;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
@Profile("!mock")
public class KakaoLoginController {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final MemberService memberService;
    private final AuthService authService;

    // 카카오 로그인 요청
    @PostMapping("/login/kakao")
    public ResponseEntity<ApiResponse<Map<String, Object>>> kakaoLogin(
            @RequestBody KakaoLoginRequest request,
            HttpServletResponse response) {
        
        try {
            String authorizationCode = request.getCode();
            if (authorizationCode == null || authorizationCode.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
            }

            // 카카오 액세스 토큰 발급
            KakaoTokenResponse tokenResponse = kakaoOAuthClient.getAccessToken(authorizationCode);
            if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(ResponseCode.UNAUTHORIZED));
            }

            // 카카오 사용자 정보 조회
            KakaoUserInfoResponse userInfo = kakaoOAuthClient.getUserInfo(tokenResponse.getAccessToken());
            if (userInfo == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(ResponseCode.UNAUTHORIZED));
            }

            // 회원 정보 처리 (가입 또는 로그인)
            String email = userInfo.getKakaoAccount().getEmail();
            String nickname = userInfo.getProperties().getNickname();
            Long kakaoId = userInfo.getId();

            // 기존 회원인지 확인하고 없으면 가입 처리
            Long memberId = memberService.processKakaoLogin(email, nickname, kakaoId);

            // JWT 토큰 생성
            TokenDto tokenDto = authService.generateTokenForSocialLogin(email, "ROLE_USER");

            // 쿠키 설정
            ResponseCookie accessTokenCookie = TokenCookieFactory.create(
                    AuthToken.ACCESS_TOKEN.name(),
                    tokenDto.getAccessToken(),
                    tokenDto.getExpiresIn());
            ResponseCookie refreshTokenCookie = TokenCookieFactory.create(
                    AuthToken.REFRESH_TOKEN.name(),
                    tokenDto.getRefreshToken(),
                    tokenDto.getRefreshExpiresIn());

            response.addHeader("Set-Cookie", accessTokenCookie.toString());
            response.addHeader("Set-Cookie", refreshTokenCookie.toString());

            // 응답 데이터 구성
            Map<String, Object> responseData = Map.of(
                    "accessToken", tokenDto.getAccessToken(),
                    "refreshToken", tokenDto.getRefreshToken(),
                    "grantType", tokenDto.getGrantType(),
                    "expiresIn", tokenDto.getExpiresIn(),
                    "refreshExpiresIn", tokenDto.getRefreshExpiresIn(),
                    "memberId", memberId,
                    "email", email,
                    "nickname", nickname
            );

            return ResponseEntity.ok(ApiResponse.success(responseData));

        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR));
        }
    }

    // 카카오 로그인 요청 DTO
    public static class KakaoLoginRequest {
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
} 