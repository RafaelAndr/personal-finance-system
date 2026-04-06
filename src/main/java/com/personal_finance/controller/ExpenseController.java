package com.personal_finance.controller;

import com.personal_finance.dto.expense.ExpenseRequestDto;
import com.personal_finance.entity.Expense;
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
    public ResponseEntity<ExpenseRequestDto> create(@RequestBody @Valid ExpenseRequestDto expenseRequestDto){
        Expense expenseSaved = expenseService.save(expenseRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseMapper.toDto(expenseSaved));
    }

    @GetMapping("{id}")
    public ResponseEntity<ExpenseRequestDto> getDetail(@PathVariable UUID id){
        return ResponseEntity.ok(expenseMapper.toDto(expenseService.getExpense(id)));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<ExpenseRequestDto>> getAllAccountExpense(@PathVariable UUID accountId){
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
    public ResponseEntity<Void> payExpense(@PathVariable UUID id){
        expenseService.payExpense(id);
        return ResponseEntity.ok().build();
    }
}
