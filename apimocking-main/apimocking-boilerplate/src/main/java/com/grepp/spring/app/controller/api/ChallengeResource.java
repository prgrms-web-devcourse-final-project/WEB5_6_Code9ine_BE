package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.challenge.model.ChallengeDTO;
import com.grepp.spring.app.model.challenge.service.ChallengeService;
import com.grepp.spring.util.ReferencedException;
import com.grepp.spring.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.context.annotation.Profile;
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

@Profile("!mock")
@RestController
@RequestMapping(value = "/api/challenges", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChallengeResource {

    private final ChallengeService challengeService;

    public ChallengeResource(final ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping
    public ResponseEntity<List<ChallengeDTO>> getAllChallenges() {
        return ResponseEntity.ok(challengeService.findAll());
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeDTO> getChallenge(
            @PathVariable(name = "challengeId") final Long challengeId) {
        return ResponseEntity.ok(challengeService.get(challengeId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createChallenge(
            @RequestBody @Valid final ChallengeDTO challengeDTO) {
        final Long createdChallengeId = challengeService.create(challengeDTO);
        return new ResponseEntity<>(createdChallengeId, HttpStatus.CREATED);
    }

    @PutMapping("/{challengeId}")
    public ResponseEntity<Long> updateChallenge(
            @PathVariable(name = "challengeId") final Long challengeId,
            @RequestBody @Valid final ChallengeDTO challengeDTO) {
        challengeService.update(challengeId, challengeDTO);
        return ResponseEntity.ok(challengeId);
    }

    @DeleteMapping("/{challengeId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteChallenge(
            @PathVariable(name = "challengeId") final Long challengeId) {
        final ReferencedWarning referencedWarning = challengeService.getReferencedWarning(challengeId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        challengeService.delete(challengeId);
        return ResponseEntity.noContent().build();
    }

}
