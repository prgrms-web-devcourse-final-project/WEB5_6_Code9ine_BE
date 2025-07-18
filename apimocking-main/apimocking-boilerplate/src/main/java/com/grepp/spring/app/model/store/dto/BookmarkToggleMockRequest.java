package com.grepp.spring.app.model.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Profile;

@Getter
@Setter
@Schema(description = "북마크 요청 DTO")
@Profile("mock")
public class BookmarkToggleMockRequest {

    @Schema(description = "식당 ID (store 북마크 시 사용)", example = "1", nullable = true)
    private Long storeId;

    @Schema(description = "축제 ID (festival 북마크 시 사용)", example = "2", nullable = true)
    private Long festivalId;

    @Schema(description = "도서관 ID (library 북마크 시 사용)", example = "3", nullable = true)
    private Long libraryId;

}
