package com.personal_finance.entity;

import com.personal_finance.entity.enums.ExpenseCategory;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "description")
    private String description;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "paid")
    private boolean paid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToOne(mappedBy = "expense")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "expense_category")
    private ExpenseCategory expenseCategory;
}
