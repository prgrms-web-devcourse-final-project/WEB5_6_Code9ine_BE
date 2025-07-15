package com.grepp.spring.app.controller.api.mock.budgetdetail;

import com.grepp.spring.app.model.budget_detail.model.BudgetDetailDto;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailRequestDTO;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailResponseDto;
import com.grepp.spring.app.model.budget_detail.model.BudgetTotalDetailResponseDto;
import com.grepp.spring.app.model.budget_detail.model.UpdatedBudgetDetailResponseDto;
import com.grepp.spring.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("mock")
@RestController
@RequestMapping("/api/budget")
public class BudgetDetailMockController {

    @Operation(summary = "총 내역 조회")
    @GetMapping("/totaldetails")
    public ApiResponse<BudgetTotalDetailResponseDto> getBudgettotaldetails() {
        // 더미 데이터 생성
        List<BudgetDetailDto> details = List.of(
            new BudgetDetailDto(101L, "식비", "지출","햄버거", LocalDate.parse("2025-07-03"), BigDecimal.valueOf(8700), "NONE"),
            new BudgetDetailDto(102L, "교통", "지출", "버스", LocalDate.parse("2025-07-02"), BigDecimal.valueOf(1250), "NONE"),
            new BudgetDetailDto(103L, "여가", "지출", "운동", LocalDate.parse("2025-07-02"), BigDecimal.valueOf(15000), "NONE")
        );

        BudgetTotalDetailResponseDto budgetDetailResponseDto = new BudgetTotalDetailResponseDto(
            "2025-07", BigDecimal.valueOf(1200000), BigDecimal.valueOf(870000), BigDecimal.valueOf(330000), details
        );

        return new ApiResponse<>("2000", "총내역 조회되었습니다", budgetDetailResponseDto);
    }

    @Operation(summary = "날짜별 내역 조회")
    @GetMapping("/detail")
    public ApiResponse<BudgetDetailResponseDto> getExpenses(@RequestParam("date") String date) {

        List<BudgetDetailDto> details = List.of(
            new BudgetDetailDto(12L,"식비","지출", "점심 도시락", LocalDate.parse(date), BigDecimal.valueOf(8000),"NONE"),
            new BudgetDetailDto(13L,"카페","지출", "아메리카노", LocalDate.parse(date), BigDecimal.valueOf(4500),"NONE"),
            new BudgetDetailDto(14L, "교통","지출", "지하철", LocalDate.parse(date), BigDecimal.valueOf(3300),"NONE")
        );

        BudgetDetailResponseDto response = new BudgetDetailResponseDto(details);

        return ApiResponse.success(response);

    }

    @Operation(summary = "날짜별 내역 추가")
    @PostMapping("/detail")
    public ApiResponse<?> addExpense(@RequestBody BudgetDetailRequestDTO dto) {

        return new ApiResponse<>("2000", "내역추가되었습니다", null);
    }

    @Operation(summary = "날짜별 내역 수정")
    @PatchMapping("/detail/{detail_id}")
    public ApiResponse<?> updateExpense(@PathVariable("detail_id") Long id, @RequestBody BudgetDetailRequestDTO dto) {

        UpdatedBudgetDetailResponseDto updated = new UpdatedBudgetDetailResponseDto(
            id,
            dto.getType(),
            LocalDate.parse(dto.getDate()),
            dto.getCategory(),
            dto.getPrice(),
            dto.getContent(),
            dto.getRepeatCycle()
        );

        return new ApiResponse<>("2000", "내역수정되었습니다.", updated);
    }

    @Operation(summary = "날짜별 내역 삭제")
    @DeleteMapping("/detail/{detail_id}")
    public ApiResponse<?> deleteExpense(@PathVariable("detail_id") Long id) {

        return new ApiResponse<>("2000", "내역삭제되었습니다", null);
    }

    @Operation(summary = "날짜별 지출 없음 등록")
    @PostMapping("/noexpenses")
    public ApiResponse<?> noExpense() {

        return new ApiResponse<>("2000", "지출없음으로되었습니다", null);
    }

}