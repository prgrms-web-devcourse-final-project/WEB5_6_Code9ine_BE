package com.grepp.spring.infra.ocr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.spring.infra.ocr.NaverOcrClient;
import com.grepp.spring.infra.ocr.dto.ReceiptDataDto;
import com.grepp.spring.infra.ocr.dto.ReceiptItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptOcrService {

    private final NaverOcrClient ocrClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReceiptDataDto extractReceipt(MultipartFile file) throws IOException {
        byte[] imageBytes = file.getBytes();
        String json = ocrClient.requestOcr(imageBytes);

        JsonNode root = objectMapper.readTree(json);
        JsonNode result = root.at("/images/0/receipt/result");

        // 매장명, 날짜, 총금액
        String storeName = result.at("/storeInfo/name/text").asText("");
        String date = result.at("/payment/date/formatted").asText("");
        String totalStr = result.at("/payment/total/price/price").asText("0");
        int totalPrice = parseIntSafe(totalStr);

        // 항목 목록
        List<ReceiptItemDto> items = new ArrayList<>();
        JsonNode itemsNode = result.at("/subResults/0/items");
        if (itemsNode.isArray()) {
            for (JsonNode item : itemsNode) {
                String name = item.at("/name/text").asText("");
                String priceStr = item.at("/price/price").asText("0");
                int price = parseIntSafe(priceStr);
                items.add(new ReceiptItemDto(name, price));
            }
        }

        return new ReceiptDataDto(storeName, date, items, totalPrice);
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
