package com.grepp.spring.app.model.budget.repos;

import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.member.domain.Member;
import feign.Param;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Budget findFirstByMember(Member member);

    Optional<Budget> findByDateAndMember(LocalDate date, Member member);

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.budgetDetails WHERE b.date = :date AND b.member = :member")
    Optional<Budget> findByDateWithDetails(@Param("date") LocalDate date, @Param("member") Member member);
}
