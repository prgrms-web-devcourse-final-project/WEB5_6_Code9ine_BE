package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.community_like.model.CommunityLikeDTO;
import com.grepp.spring.app.model.community_like.service.CommunityLikeService;
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
@RequestMapping(value = "/api/communityLikes", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommunityLikeResource {

    private final CommunityLikeService communityLikeService;

    public CommunityLikeResource(final CommunityLikeService communityLikeService) {
        this.communityLikeService = communityLikeService;
    }

    @GetMapping
    public ResponseEntity<List<CommunityLikeDTO>> getAllCommunityLikes() {
        return ResponseEntity.ok(communityLikeService.findAll());
    }

    @GetMapping("/{likeId}")
    public ResponseEntity<CommunityLikeDTO> getCommunityLike(
            @PathVariable(name = "likeId") final Long likeId) {
        return ResponseEntity.ok(communityLikeService.get(likeId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createCommunityLike(
            @RequestBody @Valid final CommunityLikeDTO communityLikeDTO) {
        final Long createdLikeId = communityLikeService.create(communityLikeDTO);
        return new ResponseEntity<>(createdLikeId, HttpStatus.CREATED);
    }

    @PutMapping("/{likeId}")
    public ResponseEntity<Long> updateCommunityLike(
            @PathVariable(name = "likeId") final Long likeId,
            @RequestBody @Valid final CommunityLikeDTO communityLikeDTO) {
        communityLikeService.update(likeId, communityLikeDTO);
        return ResponseEntity.ok(likeId);
    }

    @DeleteMapping("/{likeId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCommunityLike(
            @PathVariable(name = "likeId") final Long likeId) {
        communityLikeService.delete(likeId);
        return ResponseEntity.noContent().build();
    }

}
