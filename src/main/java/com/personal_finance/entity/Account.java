package com.personal_finance.entity;

import com.personal_finance.exception.AccessForbiddenException;
import com.personal_finance.exception.AccountHasNoUserException;
import com.personal_finance.exception.InsufficientBalanceException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "account")
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "balance")
    private BigDecimal balance;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Expense> expenses;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Income> incomes;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Payment> payments;

    public void debit(BigDecimal amount){

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (this.balance.compareTo(amount) < 0){
            throw new InsufficientBalanceException("Insufficient balance");
        }

        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount){

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        this.balance = this.balance.add(amount);
    }

    public void validateOwnership(UUID userId){

        if (this.user == null){
            throw new AccountHasNoUserException("Account has no user associated");
        }

        if (!this.user.getId().equals(userId)){
            throw new AccessForbiddenException("You are not allowed to access this account");
        }
    }

    public void changeBankName(String newBankName){
        if (newBankName == null || newBankName.isBlank()){
            throw new IllegalArgumentException("Invalid bank name");
        }
        this.bankName = newBankName;
    }
}
