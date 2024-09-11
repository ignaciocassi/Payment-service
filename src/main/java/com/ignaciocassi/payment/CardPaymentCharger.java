package com.ignaciocassi.payment;


import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface CardPaymentCharger {

    CardPaymentCharge chargeCard(
            String source,
            BigDecimal amount,
            Currency currency,
            String description
    );

}
