package com.ignaciocassi.payment.stripe;

import com.ignaciocassi.payment.CardPaymentCharge;
import com.ignaciocassi.payment.CardPaymentCharger;
import com.ignaciocassi.payment.Currency;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@ConditionalOnProperty(
        value = "stripe.enabled",
        havingValue = "false"
)
public class MockStripeService implements CardPaymentCharger {
    @Override
    public CardPaymentCharge chargeCard(String source, BigDecimal amount, Currency currency, String description) {
        return new CardPaymentCharge(true);
    }
}
