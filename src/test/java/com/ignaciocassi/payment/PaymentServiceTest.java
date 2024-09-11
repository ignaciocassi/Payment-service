package com.ignaciocassi.payment;

import com.ignaciocassi.customer.Customer;
import com.ignaciocassi.customer.CustomerRepository;
import com.ignaciocassi.message.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class PaymentServiceTest {

    private PaymentService underTest;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CardPaymentCharger cardPaymentCharger;

    @Mock
    private MessageService messageService;

    @Captor
    ArgumentCaptor<String> toArgumentCaptor;

    @Captor
    ArgumentCaptor<Payment> paymentArgumentCaptor;


    @Captor
    ArgumentCaptor<Payment> paymentMessageArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger, messageService);
    }

    @Test
    void shouldChargeCard() {
        // Given
        UUID customerId = UUID.randomUUID();

        Customer customer = new Customer(customerId, "Nacho", "2342513679");

        // PaymentRequest
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        Currency.ARS,
                        "card123x",
                        "Donation"
                )
        );

        CardPaymentCharge cardPaymentCharge = new CardPaymentCharge(true);

        // ... Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));

        // ... Card charged successfully
        given(cardPaymentCharger.chargeCard(paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()))
                .willReturn(cardPaymentCharge);

        given(messageService.sendPaymentNotificationMessage(toArgumentCaptor.capture(),
                    paymentMessageArgumentCaptor.capture()))
                .willReturn(true);

        // When
        underTest.chargeCard(customerId, paymentRequest);

        // Then
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());
        assertThat(paymentArgumentCaptor.getValue()).isEqualTo(paymentRequest.getPayment());
        assertThat(paymentArgumentCaptor.getValue().getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void shouldNotChargeCardWhenCustomerNotExists() {
        // Given
        UUID customerId = UUID.randomUUID();

        Customer customer = new Customer(customerId, "Nacho", "2342513679");

        // PaymentRequest
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        Currency.ARS,
                        "card123x",
                        "Donation"
                )
        );

        // ... Customer does not exist
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("No Customer found with id [%s] ", customerId.toString()));

        // ... No interactios with PaymentCharger nor PaymentRepository
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void shouldNotChargeCardWhenCurrencyNotSupported() {
        // Given
        UUID customerId = UUID.randomUUID();

        Customer customer = new Customer(customerId, "Nacho", "2342513679");

        // PaymentRequest
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        Currency.GBP,
                        "card123x",
                        "Donation"
                )
        );

        // When
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));

        // Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("The Currency is not supported"));

        // ... No interactios with PaymentCharger nor PaymentRepository
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrowWhenCardNotCharged() {
        // Given
        UUID customerId = UUID.randomUUID();

        // PaymentRequest
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        Currency.ARS,
                        "card123x",
                        "Donation"
                )
        );

        // ... Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        // ... Card not charged
        given(cardPaymentCharger.chargeCard(paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()))
                .willReturn(new CardPaymentCharge(false));

        // When
        // Then
        then(paymentRepository).shouldHaveNoInteractions();
        assertThatThrownBy(() ->
                underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("The Card was not charged. Payment failed.");
    }

}