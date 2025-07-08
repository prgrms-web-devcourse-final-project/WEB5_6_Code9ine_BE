package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.budget_detail.model.BudgetDetailDTO;
import com.grepp.spring.app.model.budget_detail.service.BudgetDetailService;
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
@RequestMapping(value = "/api/budgetDetails", produces = MediaType.APPLICATION_JSON_VALUE)
public class BudgetDetailResource {

    private final BudgetDetailService budgetDetailService;

    public BudgetDetailResource(final BudgetDetailService budgetDetailService) {
        this.budgetDetailService = budgetDetailService;
    }

    @GetMapping
    public ResponseEntity<List<BudgetDetailDTO>> getAllBudgetDetails() {
        return ResponseEntity.ok(budgetDetailService.findAll());
    }

    @GetMapping("/{budgetDetailId}")
    public ResponseEntity<BudgetDetailDTO> getBudgetDetail(
            @PathVariable(name = "budgetDetailId") final Long budgetDetailId) {
        return ResponseEntity.ok(budgetDetailService.get(budgetDetailId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createBudgetDetail(
            @RequestBody @Valid final BudgetDetailDTO budgetDetailDTO) {
        final Long createdBudgetDetailId = budgetDetailService.create(budgetDetailDTO);
        return new ResponseEntity<>(createdBudgetDetailId, HttpStatus.CREATED);
    }

    @PutMapping("/{budgetDetailId}")
    public ResponseEntity<Long> updateBudgetDetail(
            @PathVariable(name = "budgetDetailId") final Long budgetDetailId,
            @RequestBody @Valid final BudgetDetailDTO budgetDetailDTO) {
        budgetDetailService.update(budgetDetailId, budgetDetailDTO);
        return ResponseEntity.ok(budgetDetailId);
    }

    @DeleteMapping("/{budgetDetailId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteBudgetDetail(
            @PathVariable(name = "budgetDetailId") final Long budgetDetailId) {
        budgetDetailService.delete(budgetDetailId);
        return ResponseEntity.noContent().build();
    }

}
