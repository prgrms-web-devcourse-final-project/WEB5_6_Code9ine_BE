package com.grepp.spring.app.model.festival.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FestivalDTO {

    private Long festivalId;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 255)
    private String location;

    @Size(max = 255)
    private String category;

    @Size(max = 255)
    private String address;

    private LocalDate startAt;

    @Size(max = 255)
    private String target;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private Boolean activated;

    @Size(max = 255)
    private String url;

    private Double longitude;

    private Double latitude;

    private LocalDate endAt;

}
