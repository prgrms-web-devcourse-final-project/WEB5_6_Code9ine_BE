package com.grepp.spring.app.controller.api.mainpage;

import com.grepp.spring.app.model.budget.model.AverageSavingResponse;
import com.grepp.spring.app.model.budget.service.MainPageBudgetService;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MainController {

    private final MainPageBudgetService mainPageBudgetService;

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
}
