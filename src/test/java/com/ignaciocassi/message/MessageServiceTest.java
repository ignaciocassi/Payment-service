package com.ignaciocassi.message;

import com.ignaciocassi.payment.Currency;
import com.ignaciocassi.payment.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

class MessageServiceTest {

    private MessageService underTest;

    @Mock
    private MessageSender messageSender;

    @Captor
    private ArgumentCaptor<Message> messageArgumentCaptor;


    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        underTest = new MessageService(messageSender);
    }

    @Test
    void shouldSendPaymentNotificationMessage() {
        // Given

        // ... Customer email
        String to = "nacho.cassi97@gmail.com";

        // ... Payment
        Long id = Long.valueOf("1");
        UUID customerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("10.00");
        Currency currency = Currency.ARS;
        String source = "Card123";
        String description = "Donation";
        Payment payment = new Payment(id, customerId, amount, currency, source, description);

        // ... Message to be sent
        String subject = "You made a purchase with your credit card.";
        String body = "Your credit card was charged: \n"
                + "Description: " + payment.getDescription() + "\n"
                + "Amount: " + payment.getCurrency().toString()
                + " " + payment.getAmount() + ".";
        Message expectedMessage = new Message(to, subject, body);

        given(messageSender.sendMessage(messageArgumentCaptor.capture()))
                .willReturn(true);

        // When
        boolean sent = underTest.sendPaymentNotificationMessage(to, payment);

        // Then
        assertThat(sent).isTrue();

        assertThat(messageArgumentCaptor.getValue())
                .isEqualToComparingFieldByField(expectedMessage);
    }

    @Test
    void shouldNotSendMessageWhenEmailIsNull() {
        // Given
        String email = null;
        Payment payment = new Payment(1L, UUID.randomUUID(), new BigDecimal("10.00"), Currency.ARS, "Card123", "Donation");

        // When
        boolean sent = underTest.sendPaymentNotificationMessage(email, payment);

        // Then
        assertThat(sent).isFalse();
    }

    @Test
    void shouldNotSendMessageWhenEmailIsEmpty() {
        // Given
        String email = "";
        Payment payment = new Payment(1L, UUID.randomUUID(), new BigDecimal("10.00"), Currency.ARS, "Card123", "Donation");

        // When
        boolean sent = underTest.sendPaymentNotificationMessage(email, payment);

        // Then
        assertThat(sent).isFalse();
    }

}