package com.personal_finance.mapper;

import com.personal_finance.dto.expense.ExpenseRequestDto;
import com.personal_finance.dto.expense.ExpenseResponseDto;
import com.personal_finance.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    Expense toEntity(ExpenseRequestDto expenseRequestDto);

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "user.id", target = "userId")
    ExpenseResponseDto toDto(Expense expense);
}
