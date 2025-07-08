package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.challenge_count.model.ChallengeCountDTO;
import com.grepp.spring.app.model.challenge_count.service.ChallengeCountService;
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
@RequestMapping(value = "/api/challengeCounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChallengeCountResource {

    private final ChallengeCountService challengeCountService;

    public ChallengeCountResource(final ChallengeCountService challengeCountService) {
        this.challengeCountService = challengeCountService;
    }

    @GetMapping
    public ResponseEntity<List<ChallengeCountDTO>> getAllChallengeCounts() {
        return ResponseEntity.ok(challengeCountService.findAll());
    }

    @GetMapping("/{challengeCountId}")
    public ResponseEntity<ChallengeCountDTO> getChallengeCount(
            @PathVariable(name = "challengeCountId") final Long challengeCountId) {
        return ResponseEntity.ok(challengeCountService.get(challengeCountId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createChallengeCount(
            @RequestBody @Valid final ChallengeCountDTO challengeCountDTO) {
        final Long createdChallengeCountId = challengeCountService.create(challengeCountDTO);
        return new ResponseEntity<>(createdChallengeCountId, HttpStatus.CREATED);
    }

    @PutMapping("/{challengeCountId}")
    public ResponseEntity<Long> updateChallengeCount(
            @PathVariable(name = "challengeCountId") final Long challengeCountId,
            @RequestBody @Valid final ChallengeCountDTO challengeCountDTO) {
        challengeCountService.update(challengeCountId, challengeCountDTO);
        return ResponseEntity.ok(challengeCountId);
    }

    @DeleteMapping("/{challengeCountId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteChallengeCount(
            @PathVariable(name = "challengeCountId") final Long challengeCountId) {
        challengeCountService.delete(challengeCountId);
        return ResponseEntity.noContent().build();
    }

}
