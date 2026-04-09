package com.personal_finance.dto.user;

public record ChangePasswordDto(
        String currentPassword,
        String newPassword
) {
}
