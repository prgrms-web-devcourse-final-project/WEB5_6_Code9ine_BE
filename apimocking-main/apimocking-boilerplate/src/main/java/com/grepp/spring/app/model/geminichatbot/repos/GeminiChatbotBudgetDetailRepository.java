package com.grepp.spring.app.model.geminichatbot.repos;

import com.grepp.spring.app.model.budget_detail.domain.BudgetDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface GeminiChatbotBudgetDetailRepository extends JpaRepository<BudgetDetail, Long> {
    @Query("""
        SELECT d FROM BudgetDetail d
        WHERE d.budget.member.memberId = :memberId
          AND d.date BETWEEN :start AND :end
          AND d.type = '지출'
        ORDER BY d.date
    """)
    List<BudgetDetail> findExpenseDetailsByMemberIdAndDateBetween(
        @Param("memberId") Long memberId,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );

    @Query("""
        SELECT d FROM BudgetDetail d
        WHERE d.date BETWEEN :start AND :end
          AND d.type = '지출'
        ORDER BY d.date
    """)
    List<BudgetDetail> findAllExpenseDetailsByDateBetween(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
} 