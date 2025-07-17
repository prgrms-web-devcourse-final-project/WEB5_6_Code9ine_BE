package com.grepp.spring.app.model.store.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class DetailStorePlaceResponse {

    private Long storeId;

    private String name;
    private String address;
    private String category;
    private String contact;

    private String firstMenu;
    private Integer firstPrice;

    private String secondMenu;
    private Integer secondPrice;

    private String thirdMenu;
    private Integer thirdPrice;

    private Double longitude;
    private Double latitude;

}
