package com.grepp.spring.app.model.budget.repos;

import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.budget.model.BudgetDaySummary;
import com.grepp.spring.app.model.member.domain.Member;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Budget findFirstByMember(Member member);
    Optional<Budget> findByMemberAndDate(Member member, LocalDate date);

    // 김찬우
    @Query(value = """
            SELECT SUM(total_income - total_expense) / COUNT(DISTINCT member_id)
            FROM budget
            WHERE total_income IS NOT NULL AND total_expense IS NOT NULL
            """, nativeQuery = true)
    BigDecimal getAverageSaving();

    // 김찬우
    @Query(value = """
            SELECT SUM(total_income - total_expense)
            FROM budget
            WHERE total_income IS NOT NULL AND total_expense IS NOT NULL
            """, nativeQuery = true)
    BigDecimal getAllSaving();

    Optional<Budget> findByDateAndMember(LocalDate date, Member member);

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.budgetDetails WHERE b.date = :date AND b.member = :member")
    Optional<Budget> findByDateWithDetails(@Param("date") LocalDate date, @Param("member") Member member);

    List<Budget> findAllByMemberAndDateBetweenOrderByDateAsc(Member member, LocalDate sixMonthsAgo, LocalDate endOfThisMonth);

    @Query(value = """
    SELECT TO_CHAR(b.date, 'YYYY-MM') AS month,
           SUM(b.total_expense) AS totalExpenseSum
    FROM budget b
    JOIN member m ON b.member_id = m.member_id
    WHERE m.email = :email
      AND b.date BETWEEN :start AND :end
    GROUP BY month
    ORDER BY month
    """, nativeQuery = true)
    List<Object[]> findMonthlyExpenseSumByEmailAndDateRange(
        @Param("email") String email,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );

    @Query("SELECT SUM(b.totalIncome) " +
        "FROM Budget b " +
        "WHERE b.member.memberId = :memberId AND b.date <= :today")
    Optional<BigDecimal> sumTotalIncomeByMemberUntilToday(
        @Param("memberId") Long memberId,
        @Param("today") LocalDate today);

    @Query("SELECT SUM(b.totalExpense) " +
        "FROM Budget b " +
        "WHERE b.member.memberId = :memberId AND b.date <= :today")
    Optional<BigDecimal> sumTotalExpenseByMemberUntilToday(
        @Param("memberId") Long memberId,
        @Param("today") LocalDate today);

    @Query("""
    SELECT COUNT(b) > 0
    FROM Budget b
    WHERE b.member.memberId = :memberId
    AND b.date = :date
    """)
    boolean existsBudgetByMemberIdAndDate(@Param("memberId") Long memberId,
        @Param("date") LocalDate date);

    @Query("""
          SELECT new com.grepp.spring.app.model.budget.model.BudgetDaySummary(b.date, b.totalIncome, b.totalExpense) 
          FROM Budget b 
          WHERE b.member.memberId = :memberId AND b.date BETWEEN :start AND :end ORDER BY b.date ASC
          """)
    List<BudgetDaySummary> findMonthlySummary(@Param("memberId") Long memberId,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end);

    boolean existsByMemberAndDate(Member member, LocalDate yesterday);

    @Query("SELECT SUM(b.totalExpense) FROM Budget b WHERE b.member.memberId = :memberId AND b.date BETWEEN :start AND :end")
    BigDecimal sumExpenseByMemberAndDateBetween(
        @Param("memberId") Long memberId,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );

    // 회원 탈퇴 시 해당 회원의 모든 예산 데이터 삭제
    @Query("DELETE FROM Budget b WHERE b.member = :member")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteByMember(@Param("member") Member member);
}