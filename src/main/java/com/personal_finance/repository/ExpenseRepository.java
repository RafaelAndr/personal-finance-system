package com.personal_finance.repository;

import com.personal_finance.dto.expense.ExpenseRequestDto;
import com.personal_finance.entity.Account;
import com.personal_finance.entity.Expense;
import com.personal_finance.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findByAccount(Account account);

    @Query("SELECT e FROM Expense e WHERE e.paid = false AND e.user.id = :userId")
    List<Expense> findNotPaidExpenses(@Param("userId") UUID userId);

    @Query("SELECT e FROM Expense e WHERE e.paid = true AND e.user.id = :userId")
    List<Expense> findPaidExpenses(@Param("userId") UUID userId);

    @Query("SELECT e FROM Expense e WHERE e.paid = false AND e.account.id = :accountId")
    List<Expense> findNotPaidExpensesByAccount(@Param("accountId") UUID accountId);

    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE e.account.id = :accountId")
    BigDecimal totalAccountExpenseValue(UUID accountId);
}
