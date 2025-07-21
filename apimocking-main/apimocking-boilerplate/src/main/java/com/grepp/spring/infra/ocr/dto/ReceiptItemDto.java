package com.grepp.spring.infra.ocr.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReceiptItemDto {

    private String name;
    private int price;

    public ReceiptItemDto(String name, int price) {
        this.name = name;
        this.price = price;
    }

}
