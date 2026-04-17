package com.personal_finance.dto.account;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountBalanceDto(
        UUID id,
        String owner,
        BigDecimal balance,
        String bankName
) {
}
