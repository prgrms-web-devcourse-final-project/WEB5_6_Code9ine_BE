package com.grepp.spring.app.model.member.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MemberDTO {

    private Long memberId;

    @NotNull
    @Size(max = 50)
    @MemberEmailUnique
    private String email;

    @NotNull
    @Size(max = 255)
    private String password;

    @NotNull
    @Size(max = 50)
    private String name;

    @NotNull
    @Size(max = 255)
    private String role;

    @Size(max = 255)
    private String phoneNumber;

    private Boolean activated;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @NotNull
    @Size(max = 255)
    private String nickname;

    @Size(max = 255)
    private String profileImage;

    private Integer level;

    private Integer totalExp;

}
