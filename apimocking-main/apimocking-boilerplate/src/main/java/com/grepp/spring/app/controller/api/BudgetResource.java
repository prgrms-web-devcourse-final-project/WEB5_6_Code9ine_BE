package com.grepp.spring.app.controller.api;

import com.grepp.spring.app.model.budget.model.BudgetDTO;
import com.grepp.spring.app.model.budget.service.BudgetService;
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
@RequestMapping(value = "/api/budgets", produces = MediaType.APPLICATION_JSON_VALUE)
public class BudgetResource {

    private final BudgetService budgetService;

    public BudgetResource(final BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public ResponseEntity<List<BudgetDTO>> getAllBudgets() {
        return ResponseEntity.ok(budgetService.findAll());
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetDTO> getBudget(
            @PathVariable(name = "budgetId") final Long budgetId) {
        return ResponseEntity.ok(budgetService.get(budgetId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createBudget(@RequestBody @Valid final BudgetDTO budgetDTO) {
        final Long createdBudgetId = budgetService.create(budgetDTO);
        return new ResponseEntity<>(createdBudgetId, HttpStatus.CREATED);
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<Long> updateBudget(@PathVariable(name = "budgetId") final Long budgetId,
            @RequestBody @Valid final BudgetDTO budgetDTO) {
        budgetService.update(budgetId, budgetDTO);
        return ResponseEntity.ok(budgetId);
    }

    @DeleteMapping("/{budgetId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteBudget(@PathVariable(name = "budgetId") final Long budgetId) {
        final ReferencedWarning referencedWarning = budgetService.getReferencedWarning(budgetId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        budgetService.delete(budgetId);
        return ResponseEntity.noContent().build();
    }

}
