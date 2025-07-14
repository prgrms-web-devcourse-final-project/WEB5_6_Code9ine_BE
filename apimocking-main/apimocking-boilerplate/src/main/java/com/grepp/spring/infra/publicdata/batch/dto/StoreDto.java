package com.grepp.spring.infra.publicdata.batch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreDto {
    private String 시도;
    private String 시군;
    private String 업소명;
    private String 업종;

    private String 메뉴1;
    private String 가격1;
    private String 메뉴2;
    private String 가격2;
    private String 메뉴3;
    private String 가격3;


    private String 연락처;
    private String 주소;
}
