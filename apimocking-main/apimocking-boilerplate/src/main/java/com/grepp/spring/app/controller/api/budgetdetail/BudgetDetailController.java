package com.grepp.spring.app.controller.api.budgetdetail;

import com.grepp.spring.app.model.budget_detail.model.BudgetDetailExpenseResponseDTO;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailRequestDTO;
import com.grepp.spring.app.model.budget_detail.model.Item;
import com.grepp.spring.app.model.budget_detail.model.UpdatedExpenseResponseDto;
import com.grepp.spring.infra.response.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget")
public class BudgetDetailController {

    @GetMapping("/expenses")
    public ApiResponse<BudgetDetailExpenseResponseDTO> getExpenses(@RequestParam("date") String date) {

        List<Item> items = List.of(
            new Item(12, "식비", "🍱", "점심 도시락", 8000),
            new Item(13, "카페", "☕", "아메리카노", 4500),
            new Item(14, "교통", "🚇", "지하철", 3300)
        );

        BudgetDetailExpenseResponseDTO response = new BudgetDetailExpenseResponseDTO(items);

        return ApiResponse.success(response);

    }

    @PostMapping("/expenses")
    public ApiResponse<?> addExpense(@RequestBody BudgetDetailRequestDTO dto) {

        return new ApiResponse<>("2000", "지출추가되었습니다", null);
    }

    @PatchMapping("/expenses/{expense_id}")
    public ApiResponse<?> updateExpense(@PathVariable("expense_id") Long id, @RequestBody BudgetDetailRequestDTO dto) {

        UpdatedExpenseResponseDto updated = new UpdatedExpenseResponseDto(
            id,
            dto.getType(),
            dto.getDate(),
            dto.getCategory(),
            dto.getAmount(),
            dto.getContent(),
            dto.getRepeatCycle()
        );

        return new ApiResponse<>("2000", "지출이 수정되었습니다.", updated);
    }

    @DeleteMapping("/expenses/{expense_id}")
    public ApiResponse<?> deleteExpense(@PathVariable("expense_id") Long id) {

        return new ApiResponse<>("2000", "지출삭제되었습니다", null);
    }

    @PostMapping("/noexpenses")
    public ApiResponse<?> noExpense() {

        return new ApiResponse<>("2000", "지출없음으로되었습니다", null);
    }

}
