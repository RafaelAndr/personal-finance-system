package com.personal_finance.service;

import com.personal_finance.dto.account.AccountBalanceDto;
import com.personal_finance.dto.account.AccountRequestDto;
import com.personal_finance.dto.account.AccountTotalBalanceDto;
import com.personal_finance.dto.account.UpdateBalanceDto;
import com.personal_finance.entity.Account;
import com.personal_finance.entity.Users;
import com.personal_finance.exception.EntityAlreadyExistsException;
import com.personal_finance.repository.AccountRepository;
import com.personal_finance.security.SecurityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final SecurityService securityService;

    private Users getLoggedUser(){
        return securityService.getUserLoggedIn();
    }

    public Account save(Account account){
        Users userLogged = getLoggedUser();
        account.setUser(userLogged);

        if (existBankNameWithUser(account.getBankName())){
            throw new EntityAlreadyExistsException("Bank name already exists");
        }

        account.setBalance(BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    public Account searchById(UUID id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Account not found")
        );
    }

    private boolean existBankNameWithUser(String bankName){
        Users userLogged = getLoggedUser();
        return accountRepository.existsByBankNameAndUser(bankName, userLogged);
    }

    public void editBankName(UUID accountId, AccountRequestDto accountRequestDto){

        Account account = searchById(accountId);
        Users userLogged = getLoggedUser();

        account.validateOwnership(userLogged.getId());

        String newName = accountRequestDto.bankName();

        if (accountRepository.existsByBankNameAndUser(newName, userLogged)
                && !account.getBankName().equalsIgnoreCase(newName)) {
            throw new EntityAlreadyExistsException("Bank name already exists");
        }

        account.changeBankName(newName);

        accountRepository.save(account);
    }

    public void delete(UUID id){
        Users userLogged = getLoggedUser();

        Account account = searchById(id);

        account.validateOwnership(userLogged.getId());

        accountRepository.deleteById(id);
    }

    public List<AccountBalanceDto> getUserAccounts(){
        Users userLogged = getLoggedUser();

        List<Account> accounts = accountRepository.findByUser(userLogged);

        return accounts.stream().map(
                account -> new AccountBalanceDto(account.getId(), userLogged.getName(), account.getBalance(), account.getBankName()))
                .toList();
    }

    public AccountTotalBalanceDto getTotalUserBalance(){
        Users userLogged = getLoggedUser();

        List<Account> accounts = accountRepository.findByUser(userLogged);

        BigDecimal total = accounts.stream()
                .map(account -> account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AccountTotalBalanceDto(userLogged.getName(), total);
    }

    public void addAmount(UUID id, UpdateBalanceDto updateBalanceDto){
        Users userLogged = getLoggedUser();

        Account account = searchById(id);

        account.validateOwnership(userLogged.getId());

        account.credit(updateBalanceDto.amount());

        accountRepository.save(account);
    }

    public void removeAmount(UUID id, UpdateBalanceDto updateBalanceDto){
        Users userLogged = getLoggedUser();

        Account account = searchById(id);

        account.validateOwnership(userLogged.getId());

        account.debit(updateBalanceDto.amount());

        accountRepository.save(account);
    }
}