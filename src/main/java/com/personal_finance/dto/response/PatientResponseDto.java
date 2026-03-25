package com.personal_finance.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record PatientResponseDto(
        UUID id,
        String name,
        String cpf,
        String city,
        LocalDate registrationDate
) {
}
