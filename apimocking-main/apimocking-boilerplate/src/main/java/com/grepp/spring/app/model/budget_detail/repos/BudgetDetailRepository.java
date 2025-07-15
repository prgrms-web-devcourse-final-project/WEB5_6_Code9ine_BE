package com.grepp.spring.app.model.budget_detail.repos;

import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.budget_detail.domain.BudgetDetail;
import feign.Param;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface BudgetDetailRepository extends JpaRepository<BudgetDetail, Long> {

    BudgetDetail findFirstByBudget(Budget budget);

    boolean existsByBudget(Budget budget);

    @Query("""
          SELECT d.type, SUM(d.price) FROM BudgetDetail d 
          WHERE d.budget.member.memberId = :memberId
          AND d.date BETWEEN :start AND :end
          GROUP BY d.type
          """)
    List<Object[]> findTotalIncomeAndExpense(@Param("memberId") Long memberId,
                                             @Param("start") LocalDate start,
                                             @Param("end") LocalDate end);

    @Query("""
           SELECT d.date, d.type, SUM(d.price)
           FROM BudgetDetail d
           WHERE d.budget.member.memberId = :memberId
           AND d.date BETWEEN :start AND :end
           GROUP BY d.date, d.type
           ORDER BY d.date
          """)
    List<Object[]> findDailySummary(@Param("memberId") Long memberId,
                                    @Param("start") LocalDate start,
                                    @Param("end") LocalDate end);
}
