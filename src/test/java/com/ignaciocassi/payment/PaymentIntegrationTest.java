package com.ignaciocassi.payment;

import com.ignaciocassi.customer.Customer;
import com.ignaciocassi.customer.CustomerRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void shouldCreatePaymentSuccessfully() throws Exception {
        // Given

        // ... Customer
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "Nacho", "+542342513679");

        // ... Customer request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... Customer registration is performed
        ResultActions customerRegResultActions = mockMvc.perform(put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(request))
        );

        // ... Payment
        Payment payment = new Payment(1L,
                customerId,
                new BigDecimal("20.00"),
                Currency.ARS,
                "0x0x0x0x",
                "description");

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        // When
        // ... Payment is performed
        ResultActions paymentResultActions = mockMvc.perform(put("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(paymentRequest)));

        // Then
        // ... Assert that customer registration and payment return 200 status code
        customerRegResultActions.andExpect(status().isOk());
        paymentResultActions.andExpect(status().isOk());

        // ... Assert that payment is stored in DB
        // TODO: Implement a get payment endpoint to test with mockMvc instead of using paymentRepository
        assertThat(paymentRepository.findById(payment.getPaymentId()))
                .isPresent()
                .hasValueSatisfying(
                    p -> assertThat(p).isEqualToComparingFieldByField(payment)
        );

        // TODO: Ensure SMS is delivered
    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed to parse object to JSON");
            return null;
        }
    }

}
