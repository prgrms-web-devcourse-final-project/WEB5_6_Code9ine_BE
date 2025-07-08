package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.library.model.LibraryDTO;
import com.grepp.spring.app.model.library.service.LibraryService;
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
@RequestMapping(value = "/api/libraries", produces = MediaType.APPLICATION_JSON_VALUE)
public class LibraryResource {

    private final LibraryService libraryService;

    public LibraryResource(final LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public ResponseEntity<List<LibraryDTO>> getAllLibraries() {
        return ResponseEntity.ok(libraryService.findAll());
    }

    @GetMapping("/{libraryId}")
    public ResponseEntity<LibraryDTO> getLibrary(
            @PathVariable(name = "libraryId") final Long libraryId) {
        return ResponseEntity.ok(libraryService.get(libraryId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createLibrary(@RequestBody @Valid final LibraryDTO libraryDTO) {
        final Long createdLibraryId = libraryService.create(libraryDTO);
        return new ResponseEntity<>(createdLibraryId, HttpStatus.CREATED);
    }

    @PutMapping("/{libraryId}")
    public ResponseEntity<Long> updateLibrary(
            @PathVariable(name = "libraryId") final Long libraryId,
            @RequestBody @Valid final LibraryDTO libraryDTO) {
        libraryService.update(libraryId, libraryDTO);
        return ResponseEntity.ok(libraryId);
    }

    @DeleteMapping("/{libraryId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteLibrary(
            @PathVariable(name = "libraryId") final Long libraryId) {
        final ReferencedWarning referencedWarning = libraryService.getReferencedWarning(libraryId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        libraryService.delete(libraryId);
        return ResponseEntity.noContent().build();
    }

}
