package com.grepp.spring.app.model.community_comment.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommunityCommentDTO {

    private Long commentId;

    @NotNull
    private Long postId;

    @NotNull
    private Long memberId;

    @NotNull
    @Size(max = 1000)
    private String comment;

    @NotNull
    private LocalDateTime createdAt;

    private Boolean activated;

    private Integer count;

    @NotNull
    private Long post;

}
