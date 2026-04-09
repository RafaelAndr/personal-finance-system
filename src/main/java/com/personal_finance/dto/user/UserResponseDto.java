package com.personal_finance.dto.user;

import com.personal_finance.entity.enums.Role;

public record UserResponseDto(
        String name,
        String username,
        Role role
) {
}
