package com.grepp.spring.infra.ocr.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ReceiptDataDto {

    private String storeName;
    private String date;
    private List<ReceiptItemDto> items;
    private int totalprice;

    public ReceiptDataDto(String storeName, String date, List<ReceiptItemDto> items, int totalprice) {
        this.storeName = storeName;
        this.date = date;
        this.items = items;
        this.totalprice = totalprice;
    }

}
