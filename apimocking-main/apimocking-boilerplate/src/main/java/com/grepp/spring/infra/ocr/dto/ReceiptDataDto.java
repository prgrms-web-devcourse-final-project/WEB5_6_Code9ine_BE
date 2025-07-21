package com.grepp.spring.infra.ocr.dto;

import java.util.List;

public record ReceiptDataDto(String storeName,
                             String date,
                             List<ReceiptItemDto> items,
                             int totalprice) {
}
