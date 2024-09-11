package com.ignaciocassi.payment.stripe;

import com.ignaciocassi.payment.CardPaymentCharge;
import com.ignaciocassi.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Map;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class StripeServiceTest {

    private StripeService underTest;

    @Mock
    private StripeApi stripeApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new StripeService(stripeApi, "fake_api_key");
    }

    @Test
    void shouldChargeCard() throws StripeException {
        // Given
        String source = "0x0x0x";
        BigDecimal amount = new BigDecimal("10.00");
        Currency currency = Currency.USD;
        String description = "Zakat";

        // ... a successful charge
        Charge charge = new Charge();
        charge.setPaid(true);

        // Mock stripeApi.charge() to return a successfull charge
        given(stripeApi.charge(anyMap(), any())).willReturn(charge);

        // When
        CardPaymentCharge cardPaymentCharge = underTest.chargeCard(source, amount, currency, description);

        // Then
        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<RequestOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);

        // Captor requestMap and options to perform assertions
        then(stripeApi).should().charge(mapArgumentCaptor.capture(), optionsArgumentCaptor.capture());

        // ... Assert on requestMap
        Map<String, Object> requestMap = mapArgumentCaptor.getValue();

        assertThat(requestMap.keySet()).hasSize(4);
        assertThat(requestMap.get("amount")).isEqualTo(amount);
        assertThat(requestMap.get("currency")).isEqualTo(currency);
        assertThat(requestMap.get("source")).isEqualTo(source);
        assertThat(requestMap.get("description")).isEqualTo(description);

        // ... Assert on options
        RequestOptions options = optionsArgumentCaptor.getValue();

        assertThat(options).isNotNull();

        // ... Assert card is debited successfully
        assertThat(cardPaymentCharge).isNotNull();
        assertThat(cardPaymentCharge.isDebited()).isTrue();
    }
}