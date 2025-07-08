package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.community_post.model.CommunityPostDTO;
import com.grepp.spring.app.model.community_post.service.CommunityPostService;
import com.grepp.spring.util.ReferencedException;
import com.grepp.spring.util.ReferencedWarning;
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
@RequestMapping(value = "/api/communityPosts", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommunityPostResource {

    private final CommunityPostService communityPostService;

    public CommunityPostResource(final CommunityPostService communityPostService) {
        this.communityPostService = communityPostService;
    }

    @GetMapping
    public ResponseEntity<List<CommunityPostDTO>> getAllCommunityPosts() {
        return ResponseEntity.ok(communityPostService.findAll());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<CommunityPostDTO> getCommunityPost(
            @PathVariable(name = "postId") final Long postId) {
        return ResponseEntity.ok(communityPostService.get(postId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createCommunityPost(
            @RequestBody @Valid final CommunityPostDTO communityPostDTO) {
        final Long createdPostId = communityPostService.create(communityPostDTO);
        return new ResponseEntity<>(createdPostId, HttpStatus.CREATED);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Long> updateCommunityPost(
            @PathVariable(name = "postId") final Long postId,
            @RequestBody @Valid final CommunityPostDTO communityPostDTO) {
        communityPostService.update(postId, communityPostDTO);
        return ResponseEntity.ok(postId);
    }

    @DeleteMapping("/{postId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCommunityPost(
            @PathVariable(name = "postId") final Long postId) {
        final ReferencedWarning referencedWarning = communityPostService.getReferencedWarning(postId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        communityPostService.delete(postId);
        return ResponseEntity.noContent().build();
    }

}
