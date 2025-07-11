package com.grepp.spring.app.model.budget_detail.domain;

import com.grepp.spring.app.model.budget.domain.Budget;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetDetail {

    @Id @GeneratedValue
    @Column(nullable = false, updatable = false)
    private Long budgetDetailId;

    @Column(nullable = false, length = 100)
    private String content;

    @Column(nullable = false, precision = 20, scale = 0)
    private int price;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String repeatCycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budgetId", nullable = false)
    private Budget budget;



}
