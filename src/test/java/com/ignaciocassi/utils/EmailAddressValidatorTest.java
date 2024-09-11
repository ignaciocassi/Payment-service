package com.ignaciocassi.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EmailAddressValidatorTest {

    private EmailAddressValidator underTest;

    @BeforeEach
    void setup() {
        underTest = new EmailAddressValidator();
    }

    @ParameterizedTest
    @CsvSource({"daniel.cassi97@gmail.com,TRUE",
            "daniel.cassi97gmail.com,FALSE",
            "daniel.cassi97.com,FALSE",
            "daniel.cassi97com,FALSE",
            "daniel.cassi97,FALSE",
    })
    void shouldValidateEmail(String email, String expected) {
        assertThat(underTest.test(email)).isEqualTo(Boolean.valueOf(expected));
    }
}