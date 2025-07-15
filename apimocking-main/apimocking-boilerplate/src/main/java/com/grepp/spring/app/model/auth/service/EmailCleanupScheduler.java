package com.grepp.spring.app.model.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailCleanupScheduler {
    
    private final EmailVerificationService emailVerificationService;
    
    // 매시간 실행하여 만료된 인증 코드 정리
    @Scheduled(fixedRate = 3600000) // 1시간 = 3600000ms
    public void cleanupExpiredVerifications() {
        log.info("만료된 이메일 인증 코드 정리 시작");
        emailVerificationService.cleanupExpiredVerifications();
        log.info("만료된 이메일 인증 코드 정리 완료");
    }
} 