package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.festival.model.FestivalDTO;
import com.grepp.spring.app.model.festival.service.FestivalService;
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
@RequestMapping(value = "/api/festivals", produces = MediaType.APPLICATION_JSON_VALUE)
public class FestivalResource {

    private final FestivalService festivalService;

    public FestivalResource(final FestivalService festivalService) {
        this.festivalService = festivalService;
    }

    @GetMapping
    public ResponseEntity<List<FestivalDTO>> getAllFestivals() {
        return ResponseEntity.ok(festivalService.findAll());
    }

    @GetMapping("/{festivalId}")
    public ResponseEntity<FestivalDTO> getFestival(
            @PathVariable(name = "festivalId") final Long festivalId) {
        return ResponseEntity.ok(festivalService.get(festivalId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createFestival(@RequestBody @Valid final FestivalDTO festivalDTO) {
        final Long createdFestivalId = festivalService.create(festivalDTO);
        return new ResponseEntity<>(createdFestivalId, HttpStatus.CREATED);
    }

    @PutMapping("/{festivalId}")
    public ResponseEntity<Long> updateFestival(
            @PathVariable(name = "festivalId") final Long festivalId,
            @RequestBody @Valid final FestivalDTO festivalDTO) {
        festivalService.update(festivalId, festivalDTO);
        return ResponseEntity.ok(festivalId);
    }

    @DeleteMapping("/{festivalId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteFestival(
            @PathVariable(name = "festivalId") final Long festivalId) {
        final ReferencedWarning referencedWarning = festivalService.getReferencedWarning(festivalId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        festivalService.delete(festivalId);
        return ResponseEntity.noContent().build();
    }

}
