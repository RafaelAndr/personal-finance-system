package com.personal_finance.controller;

import com.personal_finance.dto.expense.ExpenseRequestDto;
import com.personal_finance.dto.expense.ExpenseResponseDto;
import com.personal_finance.dto.payment.PaymentRequestDto;
import com.personal_finance.mapper.ExpenseMapper;
import com.personal_finance.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;

    @PostMapping
    public ResponseEntity<ExpenseResponseDto> create(@RequestBody @Valid ExpenseRequestDto expenseRequestDto){
        ExpenseResponseDto expenseSaved = expenseService.save(expenseRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseSaved);
    }

    @GetMapping("{id}")
    public ResponseEntity<ExpenseResponseDto> getDetail(@PathVariable UUID id){
        return ResponseEntity.ok(expenseMapper.toDto(expenseService.getExpense(id)));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<ExpenseResponseDto>> getAllAccountExpense(@PathVariable UUID accountId){
        return ResponseEntity.ok(expenseService.getAllAccountExpenses(accountId)
                .stream()
                .map(expenseMapper::toDto)
                .toList());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<Void> payExpense(@PathVariable UUID id, @RequestBody PaymentRequestDto paymentRequestDto){
        expenseService.payExpense(id, paymentRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notPaid")
    public ResponseEntity<List<ExpenseResponseDto>> findNotPaidExpenses(){
        return ResponseEntity.ok(expenseService.listNotPaidExpenses());
    }

    @GetMapping("/notPaid/{accountId}")
    public ResponseEntity<List<ExpenseResponseDto>> findNotPaidExpensesByAccount(@PathVariable UUID accountId){
        return ResponseEntity.ok(expenseService.listNotPaidExpensesByAccount(accountId));
    }

    @GetMapping("/paid")
    public ResponseEntity<List<ExpenseResponseDto>> findPaidExpenses(){
        return ResponseEntity.ok(expenseService.listPaidExpenses());
    }
}
