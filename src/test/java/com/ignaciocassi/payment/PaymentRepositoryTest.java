package com.ignaciocassi.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository underTest;

    @Test
    void shouldSavePayment() {
        // Given
        Long id = Long.valueOf("1");
        UUID customerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("10.00");
        Currency currency = Currency.ARS;
        String source = "Card123";
        String description = "Donation";
        Payment payment = new Payment(id, customerId, amount, currency, source, description);

        // When
        underTest.save(payment);

        // Then
        Optional<Payment> byId = underTest.findById(id);
        assertThat(byId)
                .isPresent()
                .hasValueSatisfying(
                        p -> {
                            assertThat(p.getPaymentId()).isEqualTo(id);
                            assertThat(p.getCustomerId()).isEqualTo(customerId);
                            assertThat(p.getAmount()).isEqualTo(amount);
                            assertThat(p.getCurrency()).isEqualTo(currency);
                            assertThat(p.getSource()).isEqualTo(source);
                            assertThat(p.getDescription()).isEqualTo(description);
                        }
                );
    }

}