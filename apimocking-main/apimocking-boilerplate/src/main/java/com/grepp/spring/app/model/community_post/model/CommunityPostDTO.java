package com.grepp.spring.app.model.community_post.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommunityPostDTO {

    private Long postId;

    @NotNull
    private Long memberId;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    @Size(max = 2000)
    private String content;

    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean activated;

    @Size(max = 255)
    private String category;

    @NotNull
    private Long member;

}
