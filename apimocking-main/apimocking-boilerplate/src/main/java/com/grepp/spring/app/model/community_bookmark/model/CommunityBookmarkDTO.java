package com.grepp.spring.app.model.community_bookmark.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommunityBookmarkDTO {

    @JsonProperty("cBookmarkId")
    private Long cBookmarkId;

    @NotNull
    private Long postId;

    @NotNull
    private Long memberId;

    @NotNull
    private LocalDateTime createdAt;

    private Boolean activated;

    @NotNull
    private Long post;

}
