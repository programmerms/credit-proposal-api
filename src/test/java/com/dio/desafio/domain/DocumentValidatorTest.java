package com.dio.desafio.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class DocumentValidatorTest {

    private final DocumentValidator validator = new DocumentValidator();

    @Test
    @DisplayName("Given a formatted valid CPF When identified Then it is normalized as PF")
    void givenAFormattedValidCpfWhenIdentifiedThenItIsNormalizedAsPf() {
        final var result = validator.identify("529.982.247-25");

        verifyGivenAFormattedValidCpfWhenIdentifiedThenItIsNormalizedAsPf(result);
    }

    @Test
    @DisplayName("Given a CPF with repeated digits When identified Then validation rejects it")
    void givenACpfWithRepeatedDigitsWhenIdentifiedThenValidationRejectsIt() {
        final var thrown = catchThrowable(() -> validator.identify("111.111.111-11"));

        verifyGivenACpfWithRepeatedDigitsWhenIdentifiedThenValidationRejectsIt(thrown);
    }

    @Test
    @DisplayName("Given a CPF with a wrong check digit When identified Then validation rejects it")
    void givenACpfWithAWrongCheckDigitWhenIdentifiedThenValidationRejectsIt() {
        final var thrown = catchThrowable(() -> validator.identify("529.982.247-24"));

        verifyGivenACpfWithAWrongCheckDigitWhenIdentifiedThenValidationRejectsIt(thrown);
    }

    @Test
    @DisplayName("Given a legacy numeric CNPJ When identified Then it is normalized as PJ")
    void givenALegacyNumericCnpjWhenIdentifiedThenItIsNormalizedAsPj() {
        final var result = validator.identify("11.222.333/0001-81");

        verifyGivenALegacyNumericCnpjWhenIdentifiedThenItIsNormalizedAsPj(result);
    }

    @Test
    @DisplayName("Given an alphanumeric CNPJ When identified Then letters are uppercased and validated")
    void givenAnAlphanumericCnpjWhenIdentifiedThenLettersAreUppercasedAndValidated() {
        final var result = validator.identify("12.abc.345/01de-35");

        verifyGivenAnAlphanumericCnpjWhenIdentifiedThenLettersAreUppercasedAndValidated(result);
    }

    private void verifyGivenAFormattedValidCpfWhenIdentifiedThenItIsNormalizedAsPf(final DocumentIdentity actual) {
        assertThat(actual.normalizedDocument()).isEqualTo("52998224725");
        assertThat(actual.personType()).isEqualTo(PersonType.PF);
    }

    private void verifyGivenACpfWithRepeatedDigitsWhenIdentifiedThenValidationRejectsIt(final Throwable actual) {
        assertThat(actual).isInstanceOf(IllegalArgumentException.class);
    }

    private void verifyGivenACpfWithAWrongCheckDigitWhenIdentifiedThenValidationRejectsIt(final Throwable actual) {
        assertThat(actual).isInstanceOf(IllegalArgumentException.class);
    }

    private void verifyGivenALegacyNumericCnpjWhenIdentifiedThenItIsNormalizedAsPj(final DocumentIdentity actual) {
        assertThat(actual.normalizedDocument()).isEqualTo("11222333000181");
        assertThat(actual.personType()).isEqualTo(PersonType.PJ);
    }

    private void verifyGivenAnAlphanumericCnpjWhenIdentifiedThenLettersAreUppercasedAndValidated(
            final DocumentIdentity actual) {
        assertThat(actual.normalizedDocument()).isEqualTo("12ABC34501DE35");
        assertThat(actual.personType()).isEqualTo(PersonType.PJ);
    }
}
