package com.grepp.spring.app.controller.api.budgetdetail;

import com.google.protobuf.Api;
import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailDto;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailRequestDTO;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailResponseDto;
import com.grepp.spring.app.model.budget_detail.model.TotalBudgetDetailResponseDto;
import com.grepp.spring.app.model.budget_detail.model.UpdatedBudgetDetailResponseDto;
import com.grepp.spring.app.model.budget_detail.service.BudgetDetailService;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
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

    private final BudgetDetailService budgetDetailService;


    @Operation(summary = "총 내역 조회")
    @GetMapping("/totaldetails")
    public ApiResponse<TotalBudgetDetailResponseDto> getAllDetails(
        @AuthenticationPrincipal Principal principal,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<BudgetDetailDto> pageResult = budgetDetailService.getAllDetails(principal.getMemberId(), page, size);

        TotalBudgetDetailResponseDto responseDto = new TotalBudgetDetailResponseDto(
            pageResult.getContent(),
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.getTotalPages(),
            pageResult.isLast()
        );

        return ApiResponse.success(responseDto);
    }

    @Operation(summary = "날짜별 내역 조회")
    @GetMapping("/detail")
    public ApiResponse<BudgetDetailResponseDto> getBudgetDetail(@AuthenticationPrincipal Principal principal, @RequestParam("date") String date) {

        BudgetDetailResponseDto response = budgetDetailService.findBudgetDetailByDate(principal.getUsername(),date);
        return ApiResponse.success(response);
    }


    @Operation(summary = "날짜별 내역 추가")
    @PostMapping("/detail")
    public ApiResponse<Void> addBudgetDetail(@AuthenticationPrincipal Principal principal, @RequestBody BudgetDetailRequestDTO dto) {

        budgetDetailService.registerBudgetDetail(principal.getUsername(),dto);
        return ApiResponse.noContent();
    }

    @Operation(summary = "날짜별 내역 수정")
    @PatchMapping("/detail/{detail_id}")
    public ApiResponse<UpdatedBudgetDetailResponseDto> updateBudgetDetail(@AuthenticationPrincipal Principal principal,
                                             @PathVariable("detail_id") Long id,
                                             @RequestBody BudgetDetailRequestDTO dto) {

        UpdatedBudgetDetailResponseDto updatedExpenseResponseDto = budgetDetailService.updateBudgetDetail(principal.getMemberId(), id, dto);
        return ApiResponse.success(updatedExpenseResponseDto);
    }

    @Operation(summary = "날짜별 내역 삭제")
    @DeleteMapping("/detail/{detail_id}")
    public ApiResponse<Void> deleteBudgetDetail(@AuthenticationPrincipal Principal principal, @PathVariable("detail_id") Long id) {

        budgetDetailService.deleteBudgetDetail(principal.getMemberId(),id);
        return ApiResponse.noContent();
    }

    @Operation(summary = "날짜별 지출 없음 등록")
    @PostMapping("/noexpenses")
    public ApiResponse<Void> noExpense(@AuthenticationPrincipal Principal principal) {

        budgetDetailService.registerNoExpense(principal.getMemberId());
        return ApiResponse.noContent();
    }

}
