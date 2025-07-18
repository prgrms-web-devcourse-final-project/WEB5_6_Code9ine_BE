package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.model.auth.service.EmailVerificationService;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;

@RestController
@RequestMapping("/api/members")
@Profile("!mock")
public class EmailVerificationController {
    
    private final EmailVerificationService emailVerificationService;
    
    public EmailVerificationController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }
    
    // 이메일 인증 코드 발송
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<EmailSendResponse>> sendEmailCode(@RequestBody @Valid EmailSendRequest request) {
        try {
            emailVerificationService.sendVerificationCode(request.getEmail());
            return ResponseEntity.ok(ApiResponse.success(new EmailSendResponse("이메일 인증 코드가 발송되었습니다.")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(ResponseCode.INTERNAL_SERVER_ERROR.code(), "이메일 발송에 실패했습니다.", null));
        }
    }
    
    // 이메일 인증 코드 검증
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<EmailVerifyResponse>> verifyEmailCode(@RequestBody @Valid EmailVerifyRequest request) {
        boolean isValid = emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success(new EmailVerifyResponse("이메일 인증이 완료되었습니다.")));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "인증 코드가 올바르지 않거나 만료되었습니다.", null));
        }
    }
    
    // 이메일 인증 상태 확인
    @GetMapping("/email/status/{email}")
    public ResponseEntity<ApiResponse<EmailStatusResponse>> checkEmailStatus(@PathVariable String email) {
        boolean isVerified = emailVerificationService.isEmailVerified(email);
        return ResponseEntity.ok(ApiResponse.success(new EmailStatusResponse(isVerified)));
    }
    
    // === DTO 클래스들 ===
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailSendRequest {
        private String email;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailSendResponse {
        private String message;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailVerifyRequest {
        private String email;
        private String code;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailVerifyResponse {
        private String message;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailStatusResponse {
        private boolean verified;
    }
} 