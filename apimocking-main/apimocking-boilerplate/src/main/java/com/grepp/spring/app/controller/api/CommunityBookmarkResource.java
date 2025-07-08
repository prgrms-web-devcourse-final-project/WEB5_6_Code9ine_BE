package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.community_bookmark.model.CommunityBookmarkDTO;
import com.grepp.spring.app.model.community_bookmark.service.CommunityBookmarkService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/communityBookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommunityBookmarkResource {

    private final CommunityBookmarkService communityBookmarkService;

    public CommunityBookmarkResource(final CommunityBookmarkService communityBookmarkService) {
        this.communityBookmarkService = communityBookmarkService;
    }

    @GetMapping
    public ResponseEntity<List<CommunityBookmarkDTO>> getAllCommunityBookmarks() {
        return ResponseEntity.ok(communityBookmarkService.findAll());
    }

    @GetMapping("/{cBookmarkId}")
    public ResponseEntity<CommunityBookmarkDTO> getCommunityBookmark(
            @PathVariable(name = "cBookmarkId") final Long cBookmarkId) {
        return ResponseEntity.ok(communityBookmarkService.get(cBookmarkId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createCommunityBookmark(
            @RequestBody @Valid final CommunityBookmarkDTO communityBookmarkDTO) {
        final Long createdCBookmarkId = communityBookmarkService.create(communityBookmarkDTO);
        return new ResponseEntity<>(createdCBookmarkId, HttpStatus.CREATED);
    }

    @PutMapping("/{cBookmarkId}")
    public ResponseEntity<Long> updateCommunityBookmark(
            @PathVariable(name = "cBookmarkId") final Long cBookmarkId,
            @RequestBody @Valid final CommunityBookmarkDTO communityBookmarkDTO) {
        communityBookmarkService.update(cBookmarkId, communityBookmarkDTO);
        return ResponseEntity.ok(cBookmarkId);
    }

    @DeleteMapping("/{cBookmarkId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCommunityBookmark(
            @PathVariable(name = "cBookmarkId") final Long cBookmarkId) {
        communityBookmarkService.delete(cBookmarkId);
        return ResponseEntity.noContent().build();
    }

}
