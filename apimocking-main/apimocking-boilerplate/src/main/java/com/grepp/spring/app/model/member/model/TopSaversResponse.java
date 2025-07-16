package com.grepp.spring.app.model.member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopSaversResponse {

    private Long memberId;
    private String nickname;
    private Integer level;
    private String profileImage;
    private String name;

}