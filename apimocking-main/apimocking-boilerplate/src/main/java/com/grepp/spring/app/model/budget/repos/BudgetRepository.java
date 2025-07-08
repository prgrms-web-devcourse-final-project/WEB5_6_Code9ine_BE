package com.grepp.spring.app.model.budget.repos;

import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Budget findFirstByMember(Member member);

}
