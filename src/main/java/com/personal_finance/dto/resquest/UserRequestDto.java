package com.personal_finance.dto.resquest;

import com.personal_finance.entity.enums.Role;

public record UserRequestDto(
        String name,
        String username,
        String password,
        Role role
) {
}
