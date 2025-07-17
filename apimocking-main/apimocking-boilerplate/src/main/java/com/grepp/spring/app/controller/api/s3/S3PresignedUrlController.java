package com.grepp.spring.app.controller.api.s3;

import com.grepp.spring.infra.s3.S3PresignedUrlService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/s3")
public class S3PresignedUrlController {
    private final S3PresignedUrlService s3PresignedUrlService;

    public S3PresignedUrlController(S3PresignedUrlService s3PresignedUrlService) {
        this.s3PresignedUrlService = s3PresignedUrlService;
    }

    // S3Presigned URL 발급 (jpg 고정)
    @GetMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrl() {
        String url = s3PresignedUrlService.generatePresignedUploadUrl();
        return ResponseEntity.ok(Map.of("presignedUrl", url));
    }

    // 허용된 파일 형식 조회
    @GetMapping("/allowed-extensions")
    public ResponseEntity<Map<String, Object>> getAllowedExtensions() {
        List<String> extensions = s3PresignedUrlService.getAllowedExtensions();
        long maxFileSize = s3PresignedUrlService.getMaxFileSize();
        return ResponseEntity.ok(Map.of(
            "allowedExtensions", extensions,
            "maxFileSize", maxFileSize,
            "maxFileSizeMB", maxFileSize / (1024 * 1024)
        ));
    }
} 