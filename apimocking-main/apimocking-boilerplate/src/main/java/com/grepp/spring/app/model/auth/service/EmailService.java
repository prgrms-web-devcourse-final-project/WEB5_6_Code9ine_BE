package com.grepp.spring.app.model.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    public void sendVerificationEmail(String to, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("[가계부 앱] 이메일 인증 코드");
            message.setText(String.format(
                "안녕하세요!\n\n" +
                "가계부 앱 회원가입을 위한 이메일 인증 코드입니다.\n\n" +
                "인증 코드: %s\n\n" +
                "이 코드는 10분 후에 만료됩니다.\n" +
                "본인이 요청하지 않은 경우 이 메일을 무시하세요.\n\n" +
                "감사합니다.",
                verificationCode
            ));
            
            mailSender.send(message);
            log.info("인증 이메일 발송 완료: {}", to);
        } catch (Exception e) {
            log.error("인증 이메일 발송 실패: {}", to, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }
    
    public void sendPasswordResetEmail(String to, String resetCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("[가계부 앱] 비밀번호 재설정");
            message.setText(String.format(
                "안녕하세요!\n\n" +
                "비밀번호 재설정을 위한 인증 코드입니다.\n\n" +
                "인증 코드: %s\n\n" +
                "이 코드는 10분 후에 만료됩니다.\n" +
                "본인이 요청하지 않은 경우 이 메일을 무시하세요.\n\n" +
                "감사합니다.",
                resetCode
            ));
            
            mailSender.send(message);
            log.info("비밀번호 재설정 이메일 발송 완료: {}", to);
        } catch (Exception e) {
            log.error("비밀번호 재설정 이메일 발송 실패: {}", to, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }
    
    public void sendTempPasswordEmail(String to, String tempPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("[가계부 앱] 임시 비밀번호 발급");
            message.setText(String.format(
                "안녕하세요!\n\n" +
                "요청하신 임시 비밀번호가 발급되었습니다.\n\n" +
                "임시 비밀번호: %s\n\n" +
                "보안을 위해 로그인 후 반드시 비밀번호를 변경해주세요.\n" +
                "본인이 요청하지 않은 경우 즉시 비밀번호를 변경하시기 바랍니다.\n\n" +
                "감사합니다.",
                tempPassword
            ));
            
            mailSender.send(message);
            log.info("임시 비밀번호 이메일 발송 완료: {}", to);
        } catch (Exception e) {
            log.error("임시 비밀번호 이메일 발송 실패: {}", to, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }
} 