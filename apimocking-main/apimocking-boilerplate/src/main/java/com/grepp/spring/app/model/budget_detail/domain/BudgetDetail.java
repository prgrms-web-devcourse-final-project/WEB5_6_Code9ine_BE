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
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class BudgetDetail {

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
    private Long budgetDetailId;


    @Column(nullable = false, length = 100)
    private String content;

    @Column(nullable = false, precision = 20, scale = 1)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budgetId", nullable = false)
    private Budget budget;

}
