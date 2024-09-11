package com.ignaciocassi.payment;

import com.ignaciocassi.customer.Customer;
import com.ignaciocassi.customer.CustomerRepository;
import com.ignaciocassi.message.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final CustomerRepository customerRepository;

    private final PaymentRepository paymentRepository;

    private final CardPaymentCharger cardPaymentCharger;

    private final MessageService messageService;

    private final List<Currency> supportedCurrencies = List.of(Currency.USD, Currency.ARS);


    public PaymentService(CustomerRepository customerRepository,
                          PaymentRepository paymentRepository,
                          CardPaymentCharger cardPaymentCharger, MessageService messageService) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
        this.messageService = messageService;
    }

    void chargeCard(UUID customerId, PaymentRequest paymentRequest) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        boolean customerExists = customerOptional.isPresent();
        if (!customerExists) {
            throw new IllegalStateException(String.format("No Customer found with id [%s] ", customerId.toString()));
        }

        Payment payment = paymentRequest.getPayment();
        if (!supportedCurrencies.contains(payment.getCurrency())) {
            throw new IllegalStateException("The Currency is not supported");
        }

        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                payment.getSource(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription());
        if (!cardPaymentCharge.isDebited()) {
            throw new IllegalStateException("The Card was not charged. Payment failed.");
        }
        payment.setCustomerId(customerId);
        paymentRepository.save(payment);

        String phoneNumber = customerOptional.get().getPhoneNumber();
        boolean messageSent = messageService.sendPaymentNotificationMessage(phoneNumber, payment);

        if (!messageSent) {
            // logger message was not sent
        }
    }
}
