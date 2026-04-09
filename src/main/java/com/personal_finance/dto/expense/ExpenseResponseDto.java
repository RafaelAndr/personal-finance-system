package com.personal_finance.dto.expense;

import com.personal_finance.entity.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseResponseDto(
        UUID id,
        UUID accountId,
        UUID userId,
        String description,
        BigDecimal value,
        ExpenseCategory expenseCategory
) {
}
