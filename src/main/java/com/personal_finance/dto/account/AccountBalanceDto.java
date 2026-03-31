package com.personal_finance.dto.account;

import java.util.UUID;

public record AccountBalanceDto(
        UUID id,
        String name,
        String balance,
        String bankName
) {
}
