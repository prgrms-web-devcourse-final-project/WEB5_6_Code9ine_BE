package com.grepp.spring.app.controller.api.placesbookmark;

import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.place_bookmark.model.BookmarkResponse;
import com.grepp.spring.app.model.place_bookmark.model.BookmarkToggleRequest;
import com.grepp.spring.app.model.place_bookmark.service.PlaceBookmarkService;
import com.grepp.spring.infra.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PlacesBookmarkController {

    private final PlaceBookmarkService placeBookmarkService;

    @PatchMapping("/users/places-bookmarks/toggle")
    public ResponseEntity<ApiResponse<BookmarkResponse>> BookmarkToggle(
            @RequestBody BookmarkToggleRequest bookmarkToggleRequest,
            @AuthenticationPrincipal Principal principal
            ) {
        Long memberId = principal.getMemberId();

        BookmarkResponse response = placeBookmarkService.toggleBookmark(memberId, bookmarkToggleRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
