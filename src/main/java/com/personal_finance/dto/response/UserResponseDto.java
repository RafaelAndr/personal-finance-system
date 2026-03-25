package com.personal_finance.dto.response;

import com.personal_finance.entity.enums.Role;

public record UserResponseDto(
        String username,
        Role role
) {
}
