package com.grepp.spring.app.model.community_like.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommunityLikeDTO {

    private Long likeId;

    @NotNull
    private Long postId;

    @NotNull
    private Long memberId;

    @NotNull
    private LocalDateTime createdAt;

    @Size(max = 255)
    private String activated;

    private Integer count;

    @NotNull
    private Long post;

}
