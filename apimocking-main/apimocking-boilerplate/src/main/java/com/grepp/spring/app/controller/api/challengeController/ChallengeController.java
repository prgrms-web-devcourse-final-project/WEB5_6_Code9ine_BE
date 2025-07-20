package com.grepp.spring.app.controller.api.challengeController;


import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.challenge.model.ChallengeStatusDto;
import com.grepp.spring.app.model.challenge.service.ChallengeService;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge")
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    public List<ChallengeStatusDto> get(@AuthenticationPrincipal Principal principal) {

        List<ChallengeStatusDto> challengeStatuses = challengeService.getChallengeStatuses(
            principal.getMemberId());
        return challengeStatuses;
    }

}
