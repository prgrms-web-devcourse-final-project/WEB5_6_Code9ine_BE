package com.grepp.spring.app.model.place_bookmark.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookmarkResponse {

    private Long pBookmarkId;
    private Boolean activated;
    private String message;

}
