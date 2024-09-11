package com.ignaciocassi.payment.stripe;

import com.ignaciocassi.payment.CardPaymentCharge;
import com.ignaciocassi.payment.CardPaymentCharger;
import com.ignaciocassi.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(
        value = "stripe.enabled",
        havingValue = "true"
)
public class StripeService implements CardPaymentCharger {

    private final RequestOptions options;

    private final StripeApi stripeApi;

    public StripeService(StripeApi stripeApi,
                         @Value("${stripe.api.key}") String apiKey) {
        this.stripeApi = stripeApi;
        this.options  = RequestOptions.builder()
                .setApiKey(apiKey)
                .build();
    }


    @Override
    public CardPaymentCharge chargeCard(String source, BigDecimal amount, Currency currency, String description) {
        Map<String, Object> params = new HashMap<>();
        params.put("source", source);
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("description", description);

        try {
            Charge charge = stripeApi.charge(params, options);
            return new CardPaymentCharge(charge.getPaid());
        } catch (StripeException e) {
            throw new IllegalStateException("Cannot make stripe charge", e);
        }
    }

}
