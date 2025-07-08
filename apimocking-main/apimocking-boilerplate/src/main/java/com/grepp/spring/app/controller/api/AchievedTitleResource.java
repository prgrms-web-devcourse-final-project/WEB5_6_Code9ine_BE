package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.achieved_title.model.AchievedTitleDTO;
import com.grepp.spring.app.model.achieved_title.service.AchievedTitleService;
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
@RequestMapping(value = "/api/achievedTitles", produces = MediaType.APPLICATION_JSON_VALUE)
public class AchievedTitleResource {

    private final AchievedTitleService achievedTitleService;

    public AchievedTitleResource(final AchievedTitleService achievedTitleService) {
        this.achievedTitleService = achievedTitleService;
    }

    @GetMapping
    public ResponseEntity<List<AchievedTitleDTO>> getAllAchievedTitles() {
        return ResponseEntity.ok(achievedTitleService.findAll());
    }

    @GetMapping("/{aTId}")
    public ResponseEntity<AchievedTitleDTO> getAchievedTitle(
            @PathVariable(name = "aTId") final Long aTId) {
        return ResponseEntity.ok(achievedTitleService.get(aTId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createAchievedTitle(
            @RequestBody @Valid final AchievedTitleDTO achievedTitleDTO) {
        final Long createdATId = achievedTitleService.create(achievedTitleDTO);
        return new ResponseEntity<>(createdATId, HttpStatus.CREATED);
    }

    @PutMapping("/{aTId}")
    public ResponseEntity<Long> updateAchievedTitle(@PathVariable(name = "aTId") final Long aTId,
            @RequestBody @Valid final AchievedTitleDTO achievedTitleDTO) {
        achievedTitleService.update(aTId, achievedTitleDTO);
        return ResponseEntity.ok(aTId);
    }

    @DeleteMapping("/{aTId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteAchievedTitle(@PathVariable(name = "aTId") final Long aTId) {
        achievedTitleService.delete(aTId);
        return ResponseEntity.noContent().build();
    }

}
