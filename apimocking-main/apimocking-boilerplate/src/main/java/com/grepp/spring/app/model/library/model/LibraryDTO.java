package com.grepp.spring.app.model.library.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LibraryDTO {

    private Long libraryId;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 255)
    private String address;

    private Double longitude;

    private Double latitude;

    @Size(max = 255)
    private String url;

    private Boolean activated;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

}
