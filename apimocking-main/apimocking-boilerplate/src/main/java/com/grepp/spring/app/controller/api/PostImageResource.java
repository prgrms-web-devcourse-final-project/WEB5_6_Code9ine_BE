package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.post_image.model.PostImageDTO;
import com.grepp.spring.app.model.post_image.service.PostImageService;
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
@RequestMapping(value = "/api/postImages", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostImageResource {

    private final PostImageService postImageService;

    public PostImageResource(final PostImageService postImageService) {
        this.postImageService = postImageService;
    }

    @GetMapping
    public ResponseEntity<List<PostImageDTO>> getAllPostImages() {
        return ResponseEntity.ok(postImageService.findAll());
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<PostImageDTO> getPostImage(
            @PathVariable(name = "imageId") final Long imageId) {
        return ResponseEntity.ok(postImageService.get(imageId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPostImage(
            @RequestBody @Valid final PostImageDTO postImageDTO) {
        final Long createdImageId = postImageService.create(postImageDTO);
        return new ResponseEntity<>(createdImageId, HttpStatus.CREATED);
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<Long> updatePostImage(@PathVariable(name = "imageId") final Long imageId,
            @RequestBody @Valid final PostImageDTO postImageDTO) {
        postImageService.update(imageId, postImageDTO);
        return ResponseEntity.ok(imageId);
    }

    @DeleteMapping("/{imageId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePostImage(
            @PathVariable(name = "imageId") final Long imageId) {
        postImageService.delete(imageId);
        return ResponseEntity.noContent().build();
    }

}
