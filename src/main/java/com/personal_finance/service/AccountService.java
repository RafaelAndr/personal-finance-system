package com.personal_finance.service;
import com.personal_finance.entity.Account;
import com.personal_finance.entity.Users;
import com.personal_finance.exception.AccountHasNoUserException;
import com.personal_finance.repository.AccountRepository;
import com.personal_finance.security.SecurityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final SecurityService securityService;

    public Account save(Account account){
        Users userLogged = securityService.getUserLoggedIn();
        account.setUser(userLogged);
        return accountRepository.save(account);
    }

    public Account searchById(UUID id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Account not found")
        );
    }

    public void delete(UUID id) throws AccessDeniedException{
        Users userLoggedIn = securityService.getUserLoggedIn();

        Account account = accountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Account not found " + id));

        if (account.getUser() == null){
            throw new AccountHasNoUserException("Account has no user associated");
        }

        if (!account.getUser().getId().equals(userLoggedIn.getId())){
            throw new AccessDeniedException("You are not allowed to delete this account");
        }

        accountRepository.deleteById(id);
    }
}
