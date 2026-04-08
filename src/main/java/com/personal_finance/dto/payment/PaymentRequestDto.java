package com.personal_finance.dto.payment;

import com.personal_finance.entity.enums.PaymentMethod;

public record PaymentRequestDto(
        PaymentMethod paymentMethod
) {
}
