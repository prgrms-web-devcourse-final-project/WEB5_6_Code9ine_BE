package com.grepp.spring.app.model.store.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StoreDTO {

    private Long storeId;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 255)
    private String address;

    private Double longitude;

    private Double latitude;

    @Size(max = 255)
    private String category;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private Boolean activated;

    @Size(max = 255)
    private String location;

    @Size(max = 255)
    private String firstMenu;

    private Integer firstPrice;

    @Size(max = 255)
    private String secondMenu;

    private Integer secondPrice;

    @Size(max = 255)
    private String thirdMenu;

    private Integer thirdPrice;

    @Size(max = 255)
    private String contact;

    @Size(max = 255)
    private String sido;

}
