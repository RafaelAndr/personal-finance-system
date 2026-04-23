package com.personal_finance.service;

import com.personal_finance.dto.income.IncomeRequestDto;
import com.personal_finance.dto.income.IncomeResponseDto;
import com.personal_finance.entity.Account;
import com.personal_finance.entity.Income;
import com.personal_finance.entity.Users;
import com.personal_finance.entity.enums.IncomeCategory;
import com.personal_finance.exception.AccessForbiddenException;
import com.personal_finance.mapper.IncomeMapper;
import com.personal_finance.repository.IncomeRepository;
import com.personal_finance.security.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IncomeServiceTest {
    @Mock
    private IncomeRepository incomeRepository;
    @Mock
    private IncomeMapper incomeMapper;
    @Mock
    private AccountService accountService;
    @Mock
    private SecurityService securityService;
    @InjectMocks
    private IncomeService incomeService;

    @Test
    void shouldSaveIncome_WhenDataIsValid(){
        Users user = new Users();
        user.setId(UUID.randomUUID());

        Account account = new Account();
        UUID accountId = UUID.randomUUID();
        account.setId(accountId);
        account.setUser(user);

        Income income = new Income();

        LocalDate date = LocalDate.now();

        IncomeRequestDto incomeRequestDto = new IncomeRequestDto(
                accountId,
                IncomeCategory.INVESTMENT,
                BigDecimal.valueOf(200),
                date
        );

        IncomeResponseDto incomeResponseDto = new IncomeResponseDto(
                UUID.randomUUID(),
                IncomeCategory.INVESTMENT,
                BigDecimal.valueOf(200),
                date
        );

        when(securityService.getUserLoggedIn()).thenReturn(user);
        when(accountService.searchById(accountId)).thenReturn(account);
        when(incomeMapper.toEntity(incomeRequestDto)).thenReturn(income);
        when(incomeRepository.save(income)).thenReturn(income);
        when(incomeMapper.toDto(income)).thenReturn(incomeResponseDto);

        IncomeResponseDto result = incomeService.save(incomeRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.incomeCategory()).isEqualTo(IncomeCategory.INVESTMENT);
        assertThat(result.value()).isEqualTo(BigDecimal.valueOf(200));

        verify(incomeRepository).save(income);
        verify(incomeMapper).toEntity(incomeRequestDto);
        verify(incomeMapper).toDto(income);
    }

    @Test
    void shouldSaveIncomeWithoutAccount_WhenAccountIdIsNull(){
        Users user = new Users();
        user.setId(UUID.randomUUID());

        Income income = new Income();

        IncomeRequestDto dto = new IncomeRequestDto(
                null,
                IncomeCategory.INVESTMENT,
                BigDecimal.valueOf(200),
                LocalDate.now()
        );

        when(securityService.getUserLoggedIn()).thenReturn(user);
        when(incomeMapper.toEntity(dto)).thenReturn(income);
        when(incomeRepository.save(income)).thenReturn(income);
        when(incomeMapper.toDto(income)).thenReturn(
                new IncomeResponseDto(UUID.randomUUID(), IncomeCategory.INVESTMENT, BigDecimal.valueOf(200), LocalDate.now())
        );

        incomeService.save(dto);

        assertThat(income.getAccount()).isNull();
    }

    @Test
    void saveIncomeShouldThrowException_WhenUserIsNotAccountOwner(){
        Users user = new Users();
        user.setId(UUID.randomUUID());

        Users otherUser = new Users();
        otherUser.setId(UUID.randomUUID());

        Account account = new Account();
        UUID accountId = UUID.randomUUID();
        account.setId(accountId);
        account.setUser(otherUser);

        Income income = new Income();

        LocalDate date = LocalDate.now();

        IncomeRequestDto incomeRequestDto = new IncomeRequestDto(
                accountId,
                IncomeCategory.INVESTMENT,
                BigDecimal.valueOf(200),
                date
        );

        when(securityService.getUserLoggedIn()).thenReturn(user);
        when(accountService.searchById(accountId)).thenReturn(account);
        when(incomeMapper.toEntity(incomeRequestDto)).thenReturn(income);

        assertThatThrownBy(() -> incomeService.save(incomeRequestDto))
                .isInstanceOf(AccessForbiddenException.class)
                .hasMessage("You are not allowed to access this account");
    }

    @Test
    void getIncome_ShouldReturnIncome_WhenIncomeExists(){
        UUID id = UUID.randomUUID();

        Income income = new Income();
        income.setId(id);

        when(incomeRepository.findById(id)).thenReturn(Optional.of(income));

        IncomeResponseDto dto = new IncomeResponseDto(id, IncomeCategory.INVESTMENT, BigDecimal.valueOf(0), LocalDate.now());

        when(incomeMapper.toDto(income)).thenReturn(dto);

        IncomeResponseDto result = incomeService.getIncome(id);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldReturnAllUserIncomes_WhenUserIsLogged(){
        Users user = new Users();
        user.setId(UUID.randomUUID());

        Income income = new Income();
        UUID incomeId = UUID.randomUUID();
        income.setId(incomeId);
        income.setUser(user);

        Income income2 = new Income();
        UUID income2Id = UUID.randomUUID();
        income.setId(income2Id);
        income.setUser(user);

        List<Income> incomes = List.of(income, income2);

        IncomeResponseDto incomeResponseDto = new IncomeResponseDto(incomeId, IncomeCategory.INVESTMENT, BigDecimal.valueOf(0), LocalDate.now());

        when(securityService.getUserLoggedIn()).thenReturn(user);
        when(incomeRepository.findByUser(user)).thenReturn(incomes);
        when(incomeMapper.toDto(income)).thenReturn(incomeResponseDto);

        List<IncomeResponseDto> result = incomeService.getAllUserIncome();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst()).isEqualTo(incomeResponseDto);

        verify(incomeRepository).findByUser(user);
        verify(incomeMapper).toDto(income);
    }

    @Test
    void shouldReturnAllAccountIncomes_WhenUserIsOwner(){
        Users user = new Users();
        user.setId(UUID.randomUUID());

        Account account = new Account();
        UUID accountId = UUID.randomUUID();
        account.setId(accountId);
        account.setUser(user);

        Income income = new Income();
        UUID incomeId = UUID.randomUUID();
        income.setId(incomeId);
        income.setUser(user);
        income.setAccount(account);

        List<Income> incomes = List.of(income);

        IncomeResponseDto incomeResponseDto = new IncomeResponseDto(incomeId, IncomeCategory.INVESTMENT, BigDecimal.valueOf(0), LocalDate.now());

        when(securityService.getUserLoggedIn()).thenReturn(user);
        when(accountService.searchById(accountId)).thenReturn(account);
        when(incomeRepository.findByAccount(account)).thenReturn(incomes);
        when(incomeMapper.toDto(income)).thenReturn(incomeResponseDto);

        List<IncomeResponseDto> result = incomeService.getAllAccountIncome(accountId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(incomeResponseDto);

        verify(incomeRepository).findByAccount(account);
        verify(incomeMapper).toDto(income);
    }

    @Test
    void getAllAccountIncomesShouldThrowException_WhenUserIsNotOwner(){
        Users user = new Users();
        user.setId(UUID.randomUUID());

        Users otherUser = new Users();
        otherUser.setId(UUID.randomUUID());

        Account account = new Account();
        UUID accountId = UUID.randomUUID();
        account.setId(accountId);
        account.setUser(otherUser);

        when(securityService.getUserLoggedIn()).thenReturn(user);
        when(accountService.searchById(accountId)).thenReturn(account);

        assertThatThrownBy(() -> incomeService.getAllAccountIncome(accountId))
                .isInstanceOf(AccessForbiddenException.class)
                .hasMessage("You are not allowed to access this account");
    }
}
