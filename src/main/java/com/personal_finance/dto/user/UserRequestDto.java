package com.personal_finance.dto.user;

import com.personal_finance.entity.enums.Role;

public record UserRequestDto(
        String name,
        String username,
        String password,
        String confirmPassword,
        Role role
) {
}
