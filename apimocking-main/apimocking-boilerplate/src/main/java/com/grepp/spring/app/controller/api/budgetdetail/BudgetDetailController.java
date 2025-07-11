package com.grepp.spring.app.controller.api.budgetdetail;

import com.grepp.spring.app.model.budget.service.BudgetService;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailExpenseResponseDTO;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailRequestDTO;
import com.grepp.spring.app.model.budget_detail.model.Item;
import com.grepp.spring.app.model.budget_detail.model.UpdatedExpenseResponseDto;
import com.grepp.spring.app.model.budget_detail.service.BudgetDetailService;
import com.grepp.spring.app.model.budget_detail.service.BudgetDetailServiceNew;
import com.grepp.spring.infra.response.ApiResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
@RequiredArgsConstructor
@RequestMapping("/api/budget")
public class BudgetDetailController {

    private final BudgetDetailServiceNew budgetDetailServiceNew;

    // 해당날짜 지출조회
    @GetMapping("/expenses")
    public ApiResponse<BudgetDetailExpenseResponseDTO> getExpenses(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("date") LocalDate date) {

        BudgetDetailExpenseResponseDTO response = budgetDetailServiceNew.findExpensesByDate(userDetails.getUsername(),date);
        return ApiResponse.success(response);

    }

    // 지출추가
    @PostMapping("/expenses")
    public ApiResponse<?> addExpense(@AuthenticationPrincipal UserDetails userDetails, @RequestBody BudgetDetailRequestDTO dto) {

        budgetDetailServiceNew.registerExpense(userDetails.getUsername(),dto);
        return new ApiResponse<>("2000", "지출추가되었습니다", null);
    }

    // 지출수정
    @PatchMapping("/expenses/{expense_id}")
    public ApiResponse<?> updateExpense(@PathVariable("expense_id") Long id, @RequestBody BudgetDetailRequestDTO dto) {

        UpdatedExpenseResponseDto updatedExpenseResponseDto = budgetDetailServiceNew.updateExpense(id, dto);
        return new ApiResponse<>("2000", "지출이 수정되었습니다.", updatedExpenseResponseDto);
    }

    // 지출삭제
    @DeleteMapping("/expenses/{expense_id}")
    public ApiResponse<?> deleteExpense(@PathVariable("expense_id") Long id) {

        return new ApiResponse<>("2000", "지출삭제되었습니다", null);
    }

    // 지출없음 == 0원이지만 오늘 가계부 작성을 했다라는것을 표시하기위해
    @PostMapping("/noexpenses")
    public ApiResponse<?> noExpense() {

        return new ApiResponse<>("2000", "지출없음으로되었습니다", null);
    }

}
