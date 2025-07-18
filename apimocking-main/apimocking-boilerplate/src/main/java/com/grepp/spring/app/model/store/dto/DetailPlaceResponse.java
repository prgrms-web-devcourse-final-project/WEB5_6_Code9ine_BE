package com.grepp.spring.app.model.store.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DetailPlaceResponse {

    public DetailPlaceResponse() {}

    public abstract String getType();
    public abstract String getName();
    public abstract String getAddress();
    public abstract Double getLatitude();
    public abstract Double getLongitude();

}
