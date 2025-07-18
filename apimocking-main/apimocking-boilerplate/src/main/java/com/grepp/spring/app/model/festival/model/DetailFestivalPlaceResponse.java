package com.grepp.spring.app.model.festival.model;

import com.grepp.spring.app.model.store.dto.DetailPlaceResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DetailFestivalPlaceResponse extends DetailPlaceResponse {

    private Long festivalId;
    private String name;
    private String address;
    private String category;
    private String type;
    private String target;
    private String url;

    private LocalDate startAt;
    private LocalDate endAt;

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

    public DetailFestivalPlaceResponse(Long festivalId, String name,String address, String category,String type, String target,
                                       String url, LocalDate startAt, LocalDate endAt,
                                       Double latitude, Double longitude) {
        super();
        this.festivalId = festivalId;
        this.name = name;
        this.address = address;
        this.category = category;
        this.type = type;
        this.target = target;
        this.url = url;
        this.startAt = startAt;
        this.endAt = endAt;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
