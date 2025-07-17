package com.grepp.spring.app.model.budget_detail.repos;

import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.budget.model.BudgetCategorySummary;
import com.grepp.spring.app.model.budget_detail.domain.BudgetDetail;
import feign.Param;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface BudgetDetailRepository extends JpaRepository<BudgetDetail, Long> {

    BudgetDetail findFirstByBudget(Budget budget);

    boolean existsByBudget(Budget budget);

    @Query("""
          SELECT b FROM Budget b
          WHERE b.member.memberId = :memberId
          AND b.date BETWEEN :start AND :end
          """)
    List<Budget> findTotalIncomeAndExpense(@Param("memberId") Long memberId,
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


    @Query("""
    SELECT d FROM BudgetDetail d
    WHERE d.budget.member.memberId = :memberId
      AND d.date <= :today
    ORDER BY d.date DESC , d.createdAt DESC
    """)
    Page<BudgetDetail> findAllBeforeTodayByMemberIdOrderByDateAndCreatedAt(
        @Param("memberId") Long memberId,
        @Param("today") LocalDate today,
        Pageable pageable
    );


    @Query("""
    SELECT new com.grepp.spring.app.model.budget.model.BudgetCategorySummary(
        d.category,
        SUM(d.price)
    )
    FROM BudgetDetail d
    WHERE d.type = '지출'
      AND d.budget.member.memberId = :memberId
      AND d.budget.date BETWEEN :start AND :end
    GROUP BY d.category
""")
    List<BudgetCategorySummary> findMonthlyCategoryExpense(
        @Param("memberId") Long memberId,
        @Param("startDate") LocalDate start,
        @Param("endDate") LocalDate end);
}
