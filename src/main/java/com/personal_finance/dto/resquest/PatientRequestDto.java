package com.personal_finance.dto.resquest;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record PatientRequestDto(

        @NotBlank
        String name,

        @NotBlank
        @CPF
        String cpf,

        String telephone,

        LocalDate dateOfBirth,

        AddressRequestDto address
) {
}
