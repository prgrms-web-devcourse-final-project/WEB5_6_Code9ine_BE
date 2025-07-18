package com.grepp.spring.app.model.place_bookmark.model;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkToggleRequest {

    private String type;
    private Long id;

}
