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
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Budget {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long budgetId;


    @Column
    private LocalDate date;

    @Column(precision = 20, scale = 1)
    private BigDecimal totalIncome;

    @Column(precision = 20, scale = 1)
    private BigDecimal totalExpense;

    @Column(precision = 20, scale = 1)
    private BigDecimal targetExpense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "budget")
    private Set<BudgetDetail> budgetDetails = new HashSet<>();

}
