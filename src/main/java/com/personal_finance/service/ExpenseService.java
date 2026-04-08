package com.personal_finance.service;

import com.personal_finance.dto.expense.ExpenseRequestDto;
import com.personal_finance.dto.expense.ExpenseResponseDto;
import com.personal_finance.entity.Account;
import com.personal_finance.entity.Expense;
import com.personal_finance.entity.Users;
import com.personal_finance.exception.AccessDeniedException;
import com.personal_finance.exception.AccessForbiddenException;
import com.personal_finance.exception.ExpenseAlreadyPaidException;
import com.personal_finance.exception.ExpenseNegativeException;
import com.personal_finance.mapper.ExpenseMapper;
import com.personal_finance.repository.ExpenseRepository;
import com.personal_finance.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final SecurityService securityService;
    private final AccountService accountService;

    public ExpenseResponseDto save(ExpenseRequestDto expenseRequestDto){

        Users userLoggedIn = securityService.getUserLoggedIn();

        if (expenseRequestDto.value() != null && expenseRequestDto.value().compareTo(BigDecimal.ZERO) <= 0){
            throw new ExpenseNegativeException("You can't register a negative or zero expense value");
        }

        if (expenseRequestDto.accountId() != null){
            Account account = accountService.searchById(expenseRequestDto.accountId());

            if (!account.getUser().getId().equals(userLoggedIn.getId())){
                throw new AccessForbiddenException("You can't register an expense to an account it is not yours");
            }

            Expense expense = expenseMapper.toEntity(expenseRequestDto);
            expense.setAccount(account);
            expense.setUser(userLoggedIn);
            return expenseMapper.toDto(expenseRepository.save(expense));
        }

        Expense expense = expenseMapper.toEntity(expenseRequestDto);
        expense.setUser(userLoggedIn);

        return expenseMapper.toDto(expenseRepository.save(expense));
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
