package com.ignaciocassi.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PhonenumberValidatorTest {

    // TDD: Red, Green, Refactor. Write test first, then the class.

    private PhonenumberValidator underTest;

    @BeforeEach
    void setup() {
        underTest = new PhonenumberValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "+540000000000, true",
            "540000000000, false",
            "-540000000000, false",
            "+54000000001, false",
            "+54000000000001, false"
    })
    void shouldValidatePhoneNumbers(String phonenumber, String expected) {
        assertThat(underTest.test(phonenumber))
                .isEqualTo(Boolean.valueOf(expected));
    }

    @Test
    void shouldValidatePhonenumber() {
        // Given
        String phonenumber = "+540000000000";

        // When
        boolean isValid = underTest.test(phonenumber);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldNotValidatePhonenumberWithoutPlusSign() {
        // Given
        String phonenumber = "540000000000";

        // When
        boolean isValid = underTest.test(phonenumber);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldNotValidatePhonenumberWithSomeOtherSignThanPlusSign() {
        // Given
        String phonenumber = "-540000000000";

        // When
        boolean isValid = underTest.test(phonenumber);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldNotValidatePhonenumberShorterThan13() {
        // Given
        String phonenumber = "+54000000001";

        // When
        boolean isValid = underTest.test(phonenumber);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldNotValidatePhonenumberLongerThan13() {
        // Given
        String phonenumber = "+54000000000001";

        // When
        boolean isValid = underTest.test(phonenumber);

        // Then
        assertThat(isValid).isFalse();
    }

}
