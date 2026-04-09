package com.personal_finance.service;

import com.personal_finance.dto.expense.ExpenseRequestDto;
import com.personal_finance.dto.expense.ExpenseResponseDto;
import com.personal_finance.dto.payment.PaymentRequestDto;
import com.personal_finance.entity.Account;
import com.personal_finance.entity.Expense;
import com.personal_finance.entity.Payment;
import com.personal_finance.entity.Users;
import com.personal_finance.exception.AccessForbiddenException;
import com.personal_finance.exception.ExpenseAlreadyPaidException;
import com.personal_finance.exception.ExpenseNegativeException;
import com.personal_finance.exception.InsufficientBalanceException;
import com.personal_finance.mapper.ExpenseMapper;
import com.personal_finance.repository.ExpenseRepository;
import com.personal_finance.security.SecurityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PaymentService paymentService;

    public ExpenseResponseDto save(ExpenseRequestDto expenseRequestDto){

        Users userLoggedIn = securityService.getUserLoggedIn();

        if (expenseRequestDto.value() != null && expenseRequestDto.value().compareTo(BigDecimal.ZERO) <= 0){
            throw new ExpenseNegativeException("You can't register a negative or zero expense value");
        }

        Expense expense = expenseMapper.toEntity(expenseRequestDto);
        expense.setUser(userLoggedIn);

        if (expenseRequestDto.accountId() != null){
            Account account = accountService.searchById(expenseRequestDto.accountId());

            if (!account.getUser().getId().equals(userLoggedIn.getId())){
                throw new AccessForbiddenException("You can't register an expense to an account it is not yours");
            }

            expense.setAccount(account);
            return expenseMapper.toDto(expenseRepository.save(expense));
        }

        return expenseMapper.toDto(expenseRepository.save(expense));
    }

    public Expense getExpense(UUID id){
        return expenseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Expense not found"));
    }

    public List<Expense> getAllAccountExpenses(UUID accountId){
        Account account = accountService.searchById(accountId);
        return expenseRepository.findByAccount(account);
    }

    @Transactional
    public void payExpense(UUID id, PaymentRequestDto paymentRequestDto){

        Users userLoggedIn = securityService.getUserLoggedIn();

        Expense expense = getExpense(id);

        if (expense.isPaid()) {
            throw new ExpenseAlreadyPaidException("Expense is already paid");
        }

        if (!expense.getUser().getId().equals(userLoggedIn.getId())){
            throw new AccessForbiddenException("You can't pay this expense because it is not yours");
        }

        Payment payment = new Payment();

        payment.setExpense(expense);
        payment.setUser(userLoggedIn);
        payment.setPaymentMethod(paymentRequestDto.paymentMethod());

        Account account = accountService.searchById(expense.getAccount().getId());

        if (account.getBalance().compareTo(expense.getValue()) < 0){
            throw new InsufficientBalanceException("Your balance are to small for this payment, your balance: R$" + account.getBalance() + " expense value: R$" + expense.getValue());
        }

        account.setBalance(account.getBalance().subtract(expense.getValue()));

        payment.setAccount(account);
        expense.setPaid(true);

        accountService.save(account);
        paymentService.payExpense(payment);
        expenseRepository.save(expense);
    }

    public void delete(UUID id){
        Users userLoggedIn = securityService.getUserLoggedIn();
        Expense expense = getExpense(id);

        if (!expense.getUser().getId().equals(userLoggedIn.getId())){
            throw new AccessForbiddenException("You can't delete this expense");
        }

        expenseRepository.delete(expense);
    }

    public List<ExpenseResponseDto> listNotPaidExpenses(){
        Users user = securityService.getUserLoggedIn();
        List<Expense> expenses = expenseRepository.findNotPaidExpenses(user.getId());
        return expenses.stream().map(expenseMapper::toDto).toList();
    }

    public List<ExpenseResponseDto> listNotPaidExpensesByAccount(UUID accountId){
        Users user = securityService.getUserLoggedIn();
        Account account = accountService.searchById(accountId);

        if (!user.getId().equals(account.getUser().getId())){
            throw new AccessForbiddenException("You can't access the expenses of this account");
        }

        List<Expense> expenses = expenseRepository.findNotPaidExpensesByAccount(accountId);
        return expenses.stream().map(expenseMapper::toDto).toList();
    }

    public List<ExpenseResponseDto> listPaidExpenses(){
        Users user = securityService.getUserLoggedIn();
        List<Expense> expenses = expenseRepository.findPaidExpenses(user.getId());
        return expenses.stream().map(expenseMapper::toDto).toList();
    }
}
