package com.grepp.spring.app.model.library.model;

import com.grepp.spring.app.model.store.dto.DetailPlaceResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailLibraryPlaceResponse extends DetailPlaceResponse {

    private Long libraryId;
    private String name;
    private String address;
    private String url;
    private String type;

    private Double latitude;
    private Double longitude;

    @Override
    public String getType() {
        return type;
    }

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

    public DetailLibraryPlaceResponse(Long libraryId, String name, String address,
                                      String url,String type, Double latitude, Double longitude) {
        super();
        this.libraryId = libraryId;
        this.name = name;
        this.address = address;
        this.url = url;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
