package com.grepp.spring.app.model.budget_detail.repos;

import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.budget_detail.domain.BudgetDetail;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BudgetDetailRepository extends JpaRepository<BudgetDetail, Long> {

    BudgetDetail findFirstByBudget(Budget budget);

    boolean existsByBudget(Budget budget);
}
