package com.grepp.spring.app.model.notification.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NotificationDTO {

    private Long notificationId;

    @NotNull
    private Long memberId;

    @NotNull
    @Size(max = 255)
    private String message;

    @NotNull
    @JsonProperty("isRead")
    private Boolean isRead;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private Boolean activated;

    @NotNull
    private Long member;

}
