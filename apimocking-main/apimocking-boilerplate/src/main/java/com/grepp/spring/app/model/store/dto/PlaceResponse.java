package com.grepp.spring.app.model.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PlaceResponse {

    private Long storeId;
    private Long festivalId;
    private Long libraryId;

    private String name;
    private String address;
    private String category;
    private String type; // store, festival, library

    private String contact;
    private String firstMenu;
    private Integer firstPrice;

    private Double latitude;
    private Double longitude;

    private String url;

}
