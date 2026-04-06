package com.personal_finance.service;

import com.personal_finance.dto.expense.ExpenseRequestDto;
import com.personal_finance.entity.Account;
import com.personal_finance.entity.Expense;
import com.personal_finance.exception.ExpenseAlreadyPaidException;
import com.personal_finance.mapper.ExpenseMapper;
import com.personal_finance.repository.AccountRepository;
import com.personal_finance.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public Expense save(ExpenseRequestDto expenseRequestDto){

        Account account = accountService.searchById(expenseRequestDto.accountId());
        Expense expense = expenseMapper.toEntity(expenseRequestDto);
        expense.setAccount(account);

        return expenseRepository.save(expense);
    }

    public Expense getExpense(UUID id){
        return expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    public List<Expense> getAllAccountExpenses(UUID accountId){
        Account account = accountService.searchById(accountId);
        return expenseRepository.findByAccount(account);
    }

    public void payExpense(UUID id){
        Expense expense = getExpense(id);
        Account account = accountService.searchById(expense.getAccount().getId());
        account.setBalance(account.getBalance().subtract(expense.getValue()));
        accountService.save(account);

        if (expense.isPaid()) {
            throw new ExpenseAlreadyPaidException("Expense is already paid");
        }

        expense.setPaid(true);
        expenseRepository.save(expense);
    }

    public void delete(UUID id){
        Expense expense = getExpense(id);
        Account account = accountService.searchById(expense.getAccount().getId());
        account.setBalance(account.getBalance().subtract(expense.getValue()));
        accountService.save(account);
        expenseRepository.delete(expense);
    }
}
