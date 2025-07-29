package com.grepp.spring.app.model.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MemberEquipTitleRequest {
    @Schema(description = "획득한 칭호의 aTId", example = "10001")
    @NotNull(message = "aTId는 필수입니다.")
    @JsonProperty("aTId")
    private Long aTId;
    
    // aTId가 0인 경우 검증
    public boolean isValidATId() {
        return aTId != null && aTId > 0;
    }
} 