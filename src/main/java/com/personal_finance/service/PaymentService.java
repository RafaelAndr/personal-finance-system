package com.personal_finance.service;

import com.personal_finance.entity.Payment;
import com.personal_finance.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void save(Payment payment){
        paymentRepository.save(payment);
    }
}
