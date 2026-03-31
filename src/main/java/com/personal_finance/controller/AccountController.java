package com.personal_finance.controller;

import com.personal_finance.dto.response.AccountResponseDto;
import com.personal_finance.dto.resquest.AccountRequestDto;
import com.personal_finance.entity.Account;
import com.personal_finance.mapper.AccountMapper;
import com.personal_finance.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping
    public ResponseEntity<AccountResponseDto> create(@RequestBody AccountRequestDto accountRequestDto){
        Account createdAccount = accountService.save(accountMapper.toEntity(accountRequestDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(accountMapper.toDto(createdAccount));
    }

    @GetMapping("{id}")
    public ResponseEntity<AccountResponseDto> getById(@PathVariable UUID id){
        Account account = accountService.searchById(id);
        return ResponseEntity.ok(accountMapper.toDto(account));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws AccessDeniedException {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
