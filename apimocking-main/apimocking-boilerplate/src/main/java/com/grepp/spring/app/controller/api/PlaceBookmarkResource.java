package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.place_bookmark.model.PlaceBookmarkDTO;
import com.grepp.spring.app.model.place_bookmark.service.PlaceBookmarkService;
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
@RequestMapping(value = "/api/placeBookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlaceBookmarkResource {

    private final PlaceBookmarkService placeBookmarkService;

    public PlaceBookmarkResource(final PlaceBookmarkService placeBookmarkService) {
        this.placeBookmarkService = placeBookmarkService;
    }

    @GetMapping
    public ResponseEntity<List<PlaceBookmarkDTO>> getAllPlaceBookmarks() {
        return ResponseEntity.ok(placeBookmarkService.findAll());
    }

    @GetMapping("/{pBookmarkId}")
    public ResponseEntity<PlaceBookmarkDTO> getPlaceBookmark(
            @PathVariable(name = "pBookmarkId") final Long pBookmarkId) {
        return ResponseEntity.ok(placeBookmarkService.get(pBookmarkId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPlaceBookmark(
            @RequestBody @Valid final PlaceBookmarkDTO placeBookmarkDTO) {
        final Long createdPBookmarkId = placeBookmarkService.create(placeBookmarkDTO);
        return new ResponseEntity<>(createdPBookmarkId, HttpStatus.CREATED);
    }

    @PutMapping("/{pBookmarkId}")
    public ResponseEntity<Long> updatePlaceBookmark(
            @PathVariable(name = "pBookmarkId") final Long pBookmarkId,
            @RequestBody @Valid final PlaceBookmarkDTO placeBookmarkDTO) {
        placeBookmarkService.update(pBookmarkId, placeBookmarkDTO);
        return ResponseEntity.ok(pBookmarkId);
    }

    @DeleteMapping("/{pBookmarkId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePlaceBookmark(
            @PathVariable(name = "pBookmarkId") final Long pBookmarkId) {
        placeBookmarkService.delete(pBookmarkId);
        return ResponseEntity.noContent().build();
    }

}
