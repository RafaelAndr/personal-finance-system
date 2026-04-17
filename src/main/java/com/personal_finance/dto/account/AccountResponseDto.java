package com.personal_finance.dto.account;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponseDto(
        UUID id,
        BigDecimal balance,
        String bankName
) {
}
