package com.grepp.spring.infra.publicdata.batch.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KakaoAddressResponse {

    private List<Document> documents;

    @Getter
    @Setter
    public static class Document {
        private String address_name;
        private String x;
        private String y;
    }
}
