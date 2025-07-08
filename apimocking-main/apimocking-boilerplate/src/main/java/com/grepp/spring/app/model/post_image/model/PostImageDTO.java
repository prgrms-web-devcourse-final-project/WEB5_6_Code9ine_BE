package com.grepp.spring.app.model.post_image.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PostImageDTO {

    private Long imageId;

    @NotNull
    private Long postId;

    @NotNull
    @Size(max = 255)
    private String imageUrl;

    private LocalDateTime createdAt;

    private Integer sortOrder;

    @NotNull
    private Long post;

}
