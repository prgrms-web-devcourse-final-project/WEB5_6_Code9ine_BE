package com.grepp.spring.app.model.auth.service;

import com.grepp.spring.app.model.auth.domain.EmailVerification;
import com.grepp.spring.app.model.auth.repos.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmailVerificationService {
    
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;
    
    // 6자리 숫자 인증 코드 생성
    public String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
    
    // 인증 코드 발송
    public void sendVerificationCode(String email) {
        // 기존 인증 코드가 있다면 삭제
        emailVerificationRepository.deleteByEmail(email);
        
        // 새로운 인증 코드 생성
        String verificationCode = generateVerificationCode();
        
        // 인증 코드 저장
        EmailVerification emailVerification = new EmailVerification(email, verificationCode);
        emailVerificationRepository.save(emailVerification);
        
        // 이메일 발송
        emailService.sendVerificationEmail(email, verificationCode);
        
        log.info("인증 코드 발송 완료: {}", email);
    }
    
    // 인증 코드 검증
    public boolean verifyCode(String email, String code) {
        EmailVerification verification = emailVerificationRepository
            .findByEmailAndVerificationCode(email, code)
            .orElse(null);
        
        if (verification == null) {
            log.warn("인증 코드 불일치: {}", email);
            return false;
        }
        
        if (verification.isExpired()) {
            log.warn("만료된 인증 코드: {}", email);
            emailVerificationRepository.delete(verification);
            return false;
        }
        
        if (verification.isVerified()) {
            log.warn("이미 인증된 코드: {}", email);
            return false;
        }
        
        // 인증 완료 처리
        verification.markAsVerified();
        emailVerificationRepository.save(verification);
        
        log.info("이메일 인증 완료: {}", email);
        return true;
    }
    
    // 인증 완료 여부 확인
    public boolean isEmailVerified(String email) {
        return emailVerificationRepository.findByEmail(email)
            .map(EmailVerification::isVerified)
            .orElse(false);
    }
    
    // 만료된 인증 코드 정리 (스케줄러에서 호출)
    @Transactional
    public void cleanupExpiredVerifications() {
        emailVerificationRepository.deleteExpiredVerifications();
        log.info("만료된 인증 코드 정리 완료");
    }
    
    // 개발 환경용: 이메일 인증을 자동으로 완료시키는 메서드
    @Transactional
    public void autoVerifyEmail(String email) {
        // 기존 인증 정보가 있다면 삭제
        emailVerificationRepository.deleteByEmail(email);
        
        // 새로운 인증 정보 생성 (자동 완료)
        EmailVerification emailVerification = new EmailVerification(email, "000000");
        emailVerification.markAsVerified(); // 바로 인증 완료로 설정
        emailVerificationRepository.save(emailVerification);
        
        log.info("개발 환경: 이메일 자동 인증 완료: {}", email);
    }
} 