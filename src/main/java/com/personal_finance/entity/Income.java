package com.personal_finance.entity;

import com.personal_finance.entity.enums.IncomeCategory;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "income_category")
    @Enumerated(EnumType.STRING)
    private IncomeCategory incomeCategory;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "receipt_date")
    private LocalDate receiptDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}