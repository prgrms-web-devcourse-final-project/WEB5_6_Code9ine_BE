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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Budget {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public static Budget create(LocalDate date, Member member) {
        Budget budget = new Budget();
        budget.setDate(date);
        budget.setMember(member);
        budget.setTotalIncome(BigDecimal.ZERO);
        budget.setTotalExpense(BigDecimal.ZERO);
        return budget;
    }

    public void addAmount(String type, BigDecimal amount) {
        if ("수입".equals(type)) {
            this.totalIncome = this.totalIncome.add(amount);
        } else if ("지출".equals(type)) {
            this.totalExpense = this.totalExpense.add(amount);
        }
    }

    public void minusBudgetTotal(String type, BigDecimal price) {
        if ("수입".equals(type)) {
            this.totalIncome = this.totalIncome.subtract(price);
        } else if ("지출".equals(type)) {
            this.totalExpense = this.totalExpense.subtract(price);
        }
    }
}
