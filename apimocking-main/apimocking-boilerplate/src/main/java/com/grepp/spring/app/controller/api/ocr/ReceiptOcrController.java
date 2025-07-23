package com.grepp.spring.app.controller.api.ocr;

import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.infra.ocr.dto.ReceiptDataDto;
import com.grepp.spring.infra.ocr.service.ReceiptOcrService;
import com.grepp.spring.infra.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class ReceiptOcrController {

    private final ReceiptOcrService receiptOcrService;

    @PostMapping(value = "/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ReceiptDataDto>> uploadReceipt(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Principal principal
            ) {
        Long memberId = principal.getMemberId();

        ReceiptDataDto result = receiptOcrService.extractReceiptData(file, memberId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
