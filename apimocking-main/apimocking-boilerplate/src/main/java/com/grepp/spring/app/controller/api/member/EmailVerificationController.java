package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.model.auth.service.EmailVerificationService;
import com.grepp.spring.app.model.member.dto.EmailVerificationSendRequest;
import com.grepp.spring.app.model.member.dto.EmailVerificationSendResponse;
import com.grepp.spring.app.model.member.dto.EmailVerificationVerifyRequest;
import com.grepp.spring.app.model.member.dto.EmailVerificationVerifyResponse;
import com.grepp.spring.app.model.member.dto.EmailVerificationStatusResponse;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import com.grepp.spring.infra.error.exceptions.CommonException;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<EmailVerificationSendResponse>> sendEmailCode(@RequestBody @Valid EmailVerificationSendRequest request) {
        try {
            emailVerificationService.sendVerificationCode(request.getEmail());
            return ResponseEntity.ok(ApiResponse.success(new EmailVerificationSendResponse("이메일 인증 코드가 발송되었습니다.")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(ResponseCode.INTERNAL_SERVER_ERROR.code(), "이메일 발송에 실패했습니다.", null));
        }
    }
    
    // 이메일 인증 코드 검증
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<EmailVerificationVerifyResponse>> verifyEmailCode(@RequestBody @Valid EmailVerificationVerifyRequest request) {
        boolean isValid = emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success(new EmailVerificationVerifyResponse("이메일 인증이 완료되었습니다.")));
        } else {
            throw new CommonException(ResponseCode.INVALID_EMAIL_CODE);
        }
    }
    
    // 이메일 인증 상태 확인
    @GetMapping("/email/status/{email}")
    public ResponseEntity<ApiResponse<EmailVerificationStatusResponse>> checkEmailStatus(@PathVariable String email) {
        boolean isVerified = emailVerificationService.isEmailVerified(email);
        return ResponseEntity.ok(ApiResponse.success(new EmailVerificationStatusResponse(isVerified)));
    }
} 