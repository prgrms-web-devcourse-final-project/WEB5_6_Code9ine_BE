package com.grepp.spring.app.controller.api.ocr;

import com.grepp.spring.infra.ocr.dto.ReceiptDataDto;
import com.grepp.spring.infra.ocr.service.ReceiptOcrService;
import com.grepp.spring.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/api/budget")
@RestController
@RequiredArgsConstructor
@Profile("!mock")
public class ReceiptOcrController {

    private final ReceiptOcrService receiptOcrService;

    @Operation(summary = "영수증 OCR 분석", description = "영수증 이미지를 분석해서 매장명, 항목, 총금액 추출")
    @PostMapping(value = "/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ReceiptDataDto>> uploadReceipt(
            @Parameter(description = "영수증 이미지 파일", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestPart("file") MultipartFile file) throws IOException {
        ReceiptDataDto data = receiptOcrService.extractReceipt(file);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
