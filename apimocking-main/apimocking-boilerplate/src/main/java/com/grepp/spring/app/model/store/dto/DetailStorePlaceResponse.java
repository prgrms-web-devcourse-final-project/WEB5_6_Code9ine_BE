package com.grepp.spring.app.model.store.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class DetailStorePlaceResponse extends DetailPlaceResponse {

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


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public Double getLatitude() {
        return latitude;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    public DetailStorePlaceResponse(Long storeId, String name, String address, String category, String contact,
                                    String firstMenu, Integer firstPrice, String secondMenu, Integer secondPrice,
                                    String thirdMenu, Integer thirdPrice, Double longitude, Double latitude) {
        this.storeId = storeId;
        this.name = name;
        this.address = address;
        this.category = category;
        this.contact = contact;
        this.firstMenu = firstMenu;
        this.firstPrice = firstPrice;
        this.secondMenu = secondMenu;
        this.secondPrice = secondPrice;
        this.thirdMenu = thirdMenu;
        this.thirdPrice = thirdPrice;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
