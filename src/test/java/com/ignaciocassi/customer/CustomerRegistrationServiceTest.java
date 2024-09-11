package com.ignaciocassi.customer;

import com.ignaciocassi.utils.PhonenumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    private CustomerRegistrationService underTest;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PhonenumberValidator phonenumberValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository, phonenumberValidator);
    }

    @Test
    void shouldSaveNewCustomer() {
        // Given
        String phoneNumber = "+549999000000";
        Customer customer = new Customer(UUID.randomUUID(), "Ignacio", phoneNumber);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        given(phonenumberValidator.test(phoneNumber)).willReturn(true);

        // When
        underTest.registerNewCustomer(request);

        // Then
        // Capture the request argument sent to customerRepository on save with a customerArgumentCaptor,
        // and then validate that it's equal to the expected customer to be saved.
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualTo(customer);
    }

    @Test
    void shouldSaveNewCustomerWhenIdIsNull() {
        // Given
        String phoneNumber = "+542342513679";
        Customer customer = new Customer(null, "Nacho", phoneNumber);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        given(phonenumberValidator.test(phoneNumber)).willReturn(true);

        // When
        underTest.registerNewCustomer(request);

        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }

    @Test
    void shouldThrowWhenPhoneNumberIsTaken() {
        // Given a phone number and two customers
        String phoneNumber = "+549999000000";
        Customer customer = new Customer(UUID.randomUUID(), "Ignacio", phoneNumber);
        Customer customerTwo = new Customer(UUID.randomUUID(), "Daniel", phoneNumber);

        // ... Generate a request for customer
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(phonenumberValidator.test(phoneNumber)).willReturn(true);

        // ... Return existing customerTwo
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customerTwo));

        // When
        // Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("A Customer already exists for the phone number: [%s]", phoneNumber));

        // Finally
        then(customerRepository).should(never()).save(any(Customer.class));
    }

    @Test
    void shouldThrowWhenPhonenumberIsInvalid() {
        // Given
        String phonenumber = "-549999999999";
        Customer customer = new Customer(UUID.randomUUID(), "Ignacio", phonenumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // When
        // Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Phonenumber is not valid"));

        // Finally
        then(customerRepository).should(never()).save(any(Customer.class));
    }

}