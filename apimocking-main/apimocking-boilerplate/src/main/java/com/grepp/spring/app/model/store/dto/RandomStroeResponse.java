package com.grepp.spring.app.model.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RandomStroeResponse {

    private String name;
    private String location;
    private String address;
    private String category;
    private String contact;
    private String firstMenu;
    private Integer firstPrice;

}
