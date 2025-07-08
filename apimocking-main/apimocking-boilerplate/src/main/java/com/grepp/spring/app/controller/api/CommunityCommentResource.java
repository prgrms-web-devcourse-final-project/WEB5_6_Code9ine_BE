package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.community_comment.model.CommunityCommentDTO;
import com.grepp.spring.app.model.community_comment.service.CommunityCommentService;
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
@RequestMapping(value = "/api/communityComments", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommunityCommentResource {

    private final CommunityCommentService communityCommentService;

    public CommunityCommentResource(final CommunityCommentService communityCommentService) {
        this.communityCommentService = communityCommentService;
    }

    @GetMapping
    public ResponseEntity<List<CommunityCommentDTO>> getAllCommunityComments() {
        return ResponseEntity.ok(communityCommentService.findAll());
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommunityCommentDTO> getCommunityComment(
            @PathVariable(name = "commentId") final Long commentId) {
        return ResponseEntity.ok(communityCommentService.get(commentId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createCommunityComment(
            @RequestBody @Valid final CommunityCommentDTO communityCommentDTO) {
        final Long createdCommentId = communityCommentService.create(communityCommentDTO);
        return new ResponseEntity<>(createdCommentId, HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Long> updateCommunityComment(
            @PathVariable(name = "commentId") final Long commentId,
            @RequestBody @Valid final CommunityCommentDTO communityCommentDTO) {
        communityCommentService.update(commentId, communityCommentDTO);
        return ResponseEntity.ok(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCommunityComment(
            @PathVariable(name = "commentId") final Long commentId) {
        communityCommentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }

}
