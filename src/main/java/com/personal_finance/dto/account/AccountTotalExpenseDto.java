package com.personal_finance.dto.account;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountTotalExpenseDto(
        UUID accountId,
        BigDecimal totalExpenseAmount
) {
}
