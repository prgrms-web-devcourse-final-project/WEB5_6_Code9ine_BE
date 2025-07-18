package com.grepp.spring.app.controller.api.mainpage;

import com.grepp.spring.app.model.budget.model.AllSavingResponse;
import com.grepp.spring.app.model.budget.model.AverageSavingResponse;
import com.grepp.spring.app.model.budget.service.MainPageBudgetService;
import com.grepp.spring.app.model.challenge_count.model.ChallengeTopResponse;
import com.grepp.spring.app.model.challenge_count.service.MainPageChallengeCountService;
import com.grepp.spring.app.model.member.model.TopSaversResponse;
import com.grepp.spring.app.model.member.service.MainPageMemberService;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final MainPageBudgetService mainPageBudgetService;
    private final MainPageMemberService mainPageMemberService;
    private final MainPageChallengeCountService mainPageChallengeCountService;

    @GetMapping("/average-saving")
    public ResponseEntity<ApiResponse<AverageSavingResponse>> getAverageSaving() {
        try {
            AverageSavingResponse averageSaving = mainPageBudgetService.getAverageSaving();
            return ResponseEntity.ok(ApiResponse.success(averageSaving));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }
    }

    @GetMapping("/top-savers")
    public ResponseEntity<ApiResponse<List<TopSaversResponse>>> getTopSavers() {
        try{
            List<TopSaversResponse> result = mainPageMemberService.getTopSavers();
            return ResponseEntity.ok(ApiResponse.success(result));
        }catch (Exception e){
            log.error("top-savers 조회중 오류", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }
    }

    @GetMapping("/top-challenges")
    public ResponseEntity<ApiResponse<List<ChallengeTopResponse>>> getTopChallenges() {
        try {
            List<ChallengeTopResponse> result = mainPageChallengeCountService.findTopChallengeCounts();
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }
    }

    @GetMapping("/all-saving")
    public ResponseEntity<ApiResponse<AllSavingResponse>> getAllSaving() {
        try {
            AllSavingResponse result = mainPageBudgetService.getAllSaving();
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND));
        }
    }

}