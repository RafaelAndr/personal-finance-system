package com.personal_finance.dto.resquest;

public record AddressRequestDto(
        String street,
        String number,
        String neighborhood,
        String city,
        String state,
        String zipCode
) {
}
