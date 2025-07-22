package com.grepp.spring.infra.ocr.service;


import com.grepp.spring.infra.ocr.NaverOcrClient;
import com.grepp.spring.infra.ocr.dto.ReceiptDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReceiptOcrService {

    private final NaverOcrClient naverOcrClient;

    public ReceiptDataDto extractReceiptData(MultipartFile file) {
        return naverOcrClient.requestOcr(file);
    }
}
