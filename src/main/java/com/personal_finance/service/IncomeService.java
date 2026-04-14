package com.personal_finance.service;

import com.personal_finance.dto.income.IncomeRequestDto;
import com.personal_finance.dto.income.IncomeResponseDto;
import com.personal_finance.entity.Account;
import com.personal_finance.entity.Income;
import com.personal_finance.entity.Users;
import com.personal_finance.exception.AccessDeniedException;
import com.personal_finance.exception.AccessForbiddenException;
import com.personal_finance.exception.IncomeNegativeException;
import com.personal_finance.mapper.IncomeMapper;
import com.personal_finance.repository.IncomeRepository;
import com.personal_finance.security.SecurityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final IncomeMapper incomeMapper;
    private final AccountService accountService;
    private final SecurityService securityService;

    public IncomeResponseDto save(IncomeRequestDto incomeRequestDto){

        Users userLoggedIn = securityService.getUserLoggedIn();

        if (incomeRequestDto.value() != null && incomeRequestDto.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IncomeNegativeException("Is not possible to register negative or zero income");
        }

        if (incomeRequestDto.accountId() != null) {
            Account account = accountService.searchById(incomeRequestDto.accountId());

            if (!account.getUser().getId().equals(userLoggedIn.getId())){
                throw new AccessForbiddenException("You can't register an income to an account it is not yours");
            }

            Income income = incomeMapper.toEntity(incomeRequestDto);
            income.setAccount(account);
            income.setUser(userLoggedIn);
        }

        Income income = incomeMapper.toEntity(incomeRequestDto);
        income.setUser(userLoggedIn);

        return incomeMapper.toDto(incomeRepository.save(income));
    }

    public IncomeResponseDto getIncome(UUID id){
        return incomeMapper.toDto(incomeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Income not found")));
    }

    public List<IncomeResponseDto> getAllUserIncome(){
        Users userLoggedIn = securityService.getUserLoggedIn();

        List<Income> userIncomes = incomeRepository.findByUser(userLoggedIn);

        return userIncomes.stream().map(incomeMapper::toDto).toList();
    }

    public List<IncomeResponseDto> getAllAccountIncome(UUID accountId){
        Users userLoggedIn = securityService.getUserLoggedIn();

        Account account = accountService.searchById(accountId);

        if (!account.getUser().getId().equals(userLoggedIn.getId())){
            throw new AccessDeniedException("You can't have information about this account");
        }

        List<Income> accountIncomes = incomeRepository.findByAccount(account);

        return accountIncomes.stream().map(incomeMapper::toDto).toList();
    }
}
