package com.grepp.spring.app.model.budget.domain;

import com.grepp.spring.app.model.budget_detail.domain.BudgetDetail;
import com.grepp.spring.app.model.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Budget {

    @Id @GeneratedValue
    @Column(nullable = false, updatable = false)
    private Long budgetId;

    @Column
    private LocalDate date;

    @Column(precision = 20, scale = 0)
    private BigDecimal totalIncome;

    @Column(precision = 20, scale = 0)
    private BigDecimal totalExpense;

    @Column(precision = 20, scale = 0)
    private BigDecimal targetExpense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "budget")
    private List<BudgetDetail> budgetDetails = new ArrayList<>();

}
