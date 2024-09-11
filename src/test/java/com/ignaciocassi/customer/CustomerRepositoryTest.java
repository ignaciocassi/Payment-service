package com.ignaciocassi.customer;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

// Test queries with H2 embedded database
@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode = none"
        }
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void shouldSaveCustomer() {
        // Given
        UUID expectedId = UUID.randomUUID();
        String expectedName = "Ignacio";
        String expectedPhoneNumber = "+549999000000";
        Customer customer = new Customer(expectedId, expectedName, expectedPhoneNumber);

        // When
        underTest.save(customer);

        // Then
        Optional<Customer> byId = underTest.findById(expectedId);
        AssertionsForClassTypes.assertThat(byId)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(expectedId);
                    assertThat(c.getName()).isEqualTo(expectedName);
                    assertThat(c.getPhoneNumber()).isEqualTo(expectedPhoneNumber);
                });
    }

    @Test
    void shouldNotSaveCustomerWhenNameIsNull() {
        // Given
        UUID expectedId = UUID.randomUUID();
        String expectedPhoneNumber = "+549999000000";
        Customer customer = new Customer(expectedId, null, expectedPhoneNumber);

        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldNotSelectCustomerByPhoneNumberWhenNumberDoesNotExist() {
        // Given
        String phoneNumber = "0000";

        // When
        Optional<Customer> customerOptional = underTest.selectCustomerByPhoneNumber(phoneNumber);

        // Then
        AssertionsForClassTypes.assertThat(customerOptional).isNotPresent();
    }

    @Test
    void shouldNotSaveCustomerWhenPhoneNumberIsNull() {
        // Given
        UUID expectedId = UUID.randomUUID();
        String name = "Ignacio";
        Customer customer = new Customer(expectedId, name, null);

        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldFindSavedCustomerByPhonenumber() {
        // Given
        UUID id = UUID.randomUUID();
        String phonenumber = "+549999000000";
        String name = "Ignacio";
        Customer customer = new Customer(id, name, phonenumber);
        underTest.save(customer);

        // When
        // Then
        assertThat(underTest.selectCustomerByPhoneNumber(phonenumber))
                .isPresent()
                .hasValueSatisfying(
                        c -> {
                            assertThat(c.getId()).isEqualTo(id);
                            assertThat(c.getName()).isEqualTo(name);
                            assertThat(c.getPhoneNumber()).isEqualTo(phonenumber);
                        }
                );
    }

    @Test
    void shouldNotFindCustomerByPhonenumberWhenNotExists() {
        // Given
        String phonenumber = "+549999000000";

        // When
        // Then
        assertThat(underTest.selectCustomerByPhoneNumber(phonenumber))
                .isNotPresent();
    }

}