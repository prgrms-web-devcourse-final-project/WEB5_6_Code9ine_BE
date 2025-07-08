package com.grepp.spring.app.model.place_bookmark.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlaceBookmarkDTO {

    @JsonProperty("pBookmarkId")
    private Long pBookmarkId;

    @NotNull
    private Long memberId;

    private Long storeId;

    private Long libraryId;

    private Long festivalId;

    private LocalDateTime createdAt;

    private Boolean activatedAt;

    @NotNull
    private Long member;

    private Long festival;

    private Long store;

    private Long library;

}
