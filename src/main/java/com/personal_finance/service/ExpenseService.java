package com.personal_finance.service;

import com.personal_finance.dto.account.AccountTotalExpenseDto;
import com.personal_finance.dto.expense.ExpenseRequestDto;
import com.personal_finance.dto.expense.ExpenseResponseDto;
import com.personal_finance.dto.payment.PaymentRequestDto;
import com.personal_finance.entity.Account;
import com.personal_finance.entity.Expense;
import com.personal_finance.entity.Payment;
import com.personal_finance.entity.Users;
import com.personal_finance.exception.*;
import com.personal_finance.mapper.ExpenseMapper;
import com.personal_finance.repository.ExpenseRepository;
import com.personal_finance.security.SecurityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final SecurityService securityService;
    private final AccountService accountService;
    private final PaymentService paymentService;

    private Users getLoggedUser(){
        return securityService.getUserLoggedIn();
    }

    public ExpenseResponseDto save(ExpenseRequestDto expenseRequestDto){

        Users userLoggedIn = getLoggedUser();

        log.info("Starting expense creation for userId={}", userLoggedIn.getId());

        if (expenseRequestDto.value() != null && expenseRequestDto.value().compareTo(BigDecimal.ZERO) <= 0){
            log.warn("Invalid expense value={} for userId={}", expenseRequestDto.value(), userLoggedIn.getId());
            throw new NegativeValueException("You can't register a negative or zero expense value");
        }

        Expense expense = expenseMapper.toEntity(expenseRequestDto);
        expense.setUser(userLoggedIn);

        if (expenseRequestDto.accountId() != null){
            log.info("Fetching accountId={} for userId={}", expenseRequestDto.accountId(), userLoggedIn.getId());
            Account account = accountService.searchById(expenseRequestDto.accountId());

            account.validateOwnership(userLoggedIn.getId());

            expense.setAccount(account);
        }

        Expense savedExpense = expenseRepository.save(expense);

        log.info("Expense created successfully with id={} for userId={}",
                savedExpense.getId(), userLoggedIn.getId());

        return expenseMapper.toDto(savedExpense);
    }

    public Expense getExpense(UUID id){
        log.debug("Fetching expense by id={}", id);

        return expenseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Expense not found for id={}", id);
                    return new EntityNotFoundException("Expense not found");
                });
    }

    public List<Expense> getAllAccountExpenses(UUID accountId){
        log.debug("Fetching all expenses for account id={}", accountId);

        Account account = accountService.searchById(accountId);

        List<Expense> expenses = expenseRepository.findByAccount(account);

        log.debug("Found {} expenses for account id={}", expenses.size(), accountId);

        return expenses;
    }

    @Transactional
    public void payExpense(UUID id, PaymentRequestDto paymentRequestDto){

        log.info("Initiating payment for expense id={}", id);

        Users userLoggedIn = securityService.getUserLoggedIn();
        log.debug("Authenticated user id={}", userLoggedIn.getId());

        Expense expense = getExpense(id);

        expense.validateOwnership(userLoggedIn.getId());

        if (expense.isPaid()) {
            log.warn("Attempt to pay already paid expense id={} by user id={}", id, userLoggedIn.getId());
            throw new ExpenseAlreadyPaidException("Expense is already paid");
        }

        if (!expense.getUser().getId().equals(userLoggedIn.getId())){
            log.warn("Unauthorized payment attempt: user id={} trying to pay expense id={} owned by user id={}",
                    userLoggedIn.getId(), id, expense.getUser().getId());
            throw new AccessForbiddenException("You can't pay this expense because it is not yours");
        }

        Payment payment = new Payment();
        payment.setExpense(expense);
        payment.setUser(userLoggedIn);
        payment.setPaymentMethod(paymentRequestDto.paymentMethod());
        payment.setExpenseValue(expense.getValue());
        payment.setUserName(expense.getUser().getName());

        if (expense.getAccount() != null){

            Account account = accountService.searchById(expense.getAccount().getId());

            log.debug("Account id={} balance before payment={}", account.getId(), account.getBalance());

            account.debit(expense.getValue());

            log.info("Debiting account id={} new balance={}", account.getId(), account.getBalance());

            payment.setAccount(account);
        }

        expense.markAsPaid();

        paymentService.saveExpensePayment(payment);
        expenseRepository.save(expense);

        log.info("Expense id={} paid successfully by user id={}", id, userLoggedIn.getId());
    }

    public void delete(UUID id){
        Users userLogged = getLoggedUser();

        log.info("User {} attempting to delete expense {}", userLogged.getId(), id);

        Expense expense = getExpense(id);

        expense.validateOwnership(userLogged.getId());

        expenseRepository.delete(expense);

        log.info("Expense {} deleted by user {}", id, userLogged.getId());
    }

    public List<ExpenseResponseDto> listNotPaidExpenses(){
        Users userLogged = getLoggedUser();
        log.debug("Listing not paid expenses for user {}", userLogged.getId());

        List<Expense> expenses = expenseRepository.findNotPaidExpenses(userLogged.getId());

        log.debug("Found {} not paid expenses for user {}", expenses.size(), userLogged.getId());
        return expenses.stream().map(expenseMapper::toDto).toList();
    }

    public List<ExpenseResponseDto> listNotPaidExpensesByAccount(UUID accountId){
        Users userLogged = getLoggedUser();
        log.info("User {} requesting not paid expenses for account {}", userLogged.getId(), accountId);

        Account account = accountService.searchById(accountId);

        account.validateOwnership(userLogged.getId());

        List<Expense> expenses = expenseRepository.findNotPaidExpensesByAccount(accountId);

        log.debug("Found {} not paid expenses for account {}", expenses.size(), accountId);
        return expenses.stream().map(expenseMapper::toDto).toList();
    }

    public List<ExpenseResponseDto> listPaidExpenses(){
        Users userLogged = getLoggedUser();
        log.debug("Listing paid expenses for user {}", userLogged.getId());

        List<Expense> expenses = expenseRepository.findPaidExpenses(userLogged.getId());

        return expenses.stream().map(expenseMapper::toDto).toList();
    }

    public AccountTotalExpenseDto getTotalExpenseAccountValue(UUID accountId){
        log.info("Calculating total expense value for account {}", accountId);

        BigDecimal amount = expenseRepository.totalAccountExpenseValue(accountId);

        log.info("Total expense for account {} is {}", accountId, amount);

        return new AccountTotalExpenseDto(accountId, amount);
    }
}