package com.grepp.spring.app.model.budget_detail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private int id;
    private String category;
    private String icon;
    private String content;
    private int price;

}
