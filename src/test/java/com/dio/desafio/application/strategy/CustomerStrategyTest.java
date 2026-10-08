package com.dio.desafio.application.strategy;

import com.dio.desafio.application.exception.FieldValidationException;
import com.dio.desafio.application.exception.FieldValidationException.Violation;
import com.dio.desafio.domain.PersonType;
import com.dio.desafio.domain.proposal.person.Customer;
import com.dio.desafio.domain.proposal.person.LegalPerson;
import com.dio.desafio.domain.proposal.person.NaturalPerson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.dio.desafio.support.TestConstants.CNPJ;
import static com.dio.desafio.support.TestConstants.CPF;
import static com.dio.desafio.support.command.CustomerDataSupport.defaultLegalCustomerData;
import static com.dio.desafio.support.command.CustomerDataSupport.defaultNaturalCustomerData;
import static com.dio.desafio.support.domain.AddressSupport.defaultAddress;
import static com.dio.desafio.support.domain.ContactSupport.defaultContact;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.instancio.Select.field;

class CustomerStrategyTest {

    private final NaturalPersonCustomerStrategy natural = new NaturalPersonCustomerStrategy();
    private final LegalPersonCustomerStrategy legal = new LegalPersonCustomerStrategy();
    private final CustomerStrategySelector selector = new CustomerStrategySelector(natural, legal);

    @Test
    @DisplayName("Given the PF person type When the selector chooses Then the natural Strategy is returned")
    void givenThePfPersonTypeWhenTheSelectorChoosesThenTheNaturalStrategyIsReturned() {
        final var result = selector.select(PersonType.PF);

        verifyGivenThePfPersonTypeWhenTheSelectorChoosesThenTheNaturalStrategyIsReturned(result);
    }

    @Test
    @DisplayName("Given the PJ person type When the selector chooses Then the legal Strategy is returned")
    void givenThePjPersonTypeWhenTheSelectorChoosesThenTheLegalStrategyIsReturned() {
        final var result = selector.select(PersonType.PJ);

        verifyGivenThePjPersonTypeWhenTheSelectorChoosesThenTheLegalStrategyIsReturned(result);
    }

    @Test
    @DisplayName("Given complete PF data When the natural Strategy builds Then a NaturalPerson with the CPF is returned")
    void givenCompletePfDataWhenTheNaturalStrategyBuildsThenANaturalPersonWithTheCpfIsReturned() {
        final var data = defaultNaturalCustomerData().create();

        final var result = natural.build(CPF, data, defaultAddress().create(), defaultContact().create());

        verifyGivenCompletePfDataWhenTheNaturalStrategyBuildsThenANaturalPersonWithTheCpfIsReturned(result);
    }

    @Test
    @DisplayName("Given PF data without name When the natural Strategy builds Then cliente.nome is required")
    void givenPfDataWithoutNameWhenTheNaturalStrategyBuildsThenClienteNomeIsRequired() {
        final var data = defaultNaturalCustomerData()
                .set(field(com.dio.desafio.application.port.in.ContractProposalCommand.CustomerData::name), null)
                .create();

        final var thrown = catchThrowable(
                () -> natural.build(CPF, data, defaultAddress().create(), defaultContact().create()));

        verifyGivenPfDataWithoutNameWhenTheNaturalStrategyBuildsThenClienteNomeIsRequired(thrown);
    }

    @Test
    @DisplayName("Given complete PJ data When the legal Strategy builds Then a LegalPerson with the CNPJ is returned")
    void givenCompletePjDataWhenTheLegalStrategyBuildsThenALegalPersonWithTheCnpjIsReturned() {
        final var data = defaultLegalCustomerData().create();

        final var result = legal.build(CNPJ, data, defaultAddress().create(), defaultContact().create());

        verifyGivenCompletePjDataWhenTheLegalStrategyBuildsThenALegalPersonWithTheCnpjIsReturned(result);
    }

    @Test
    @DisplayName("Given PF data sent for a PJ customer When the legal Strategy builds Then cliente.razaoSocial is required")
    void givenPfDataSentForAPjCustomerWhenTheLegalStrategyBuildsThenClienteRazaoSocialIsRequired() {
        final var data = defaultNaturalCustomerData().create();

        final var thrown = catchThrowable(
                () -> legal.build(CNPJ, data, defaultAddress().create(), defaultContact().create()));

        verifyGivenPfDataSentForAPjCustomerWhenTheLegalStrategyBuildsThenClienteRazaoSocialIsRequired(thrown);
    }

    @Test
    @DisplayName("Given PJ data sent for a PF customer When the natural Strategy builds Then all four missing fields are listed")
    void givenPjDataSentForAPfCustomerWhenTheNaturalStrategyBuildsThenAllFourMissingFieldsAreListed() {
        final var data = defaultLegalCustomerData().create();

        final var thrown = catchThrowable(
                () -> natural.build(CPF, data, defaultAddress().create(), defaultContact().create()));

        verifyGivenPjDataSentForAPfCustomerWhenTheNaturalStrategyBuildsThenAllFourMissingFieldsAreListed(thrown);
    }

    private void verifyGivenThePfPersonTypeWhenTheSelectorChoosesThenTheNaturalStrategyIsReturned(
            final CustomerStrategy actual) {
        assertThat(actual).isSameAs(natural);
    }

    private void verifyGivenThePjPersonTypeWhenTheSelectorChoosesThenTheLegalStrategyIsReturned(
            final CustomerStrategy actual) {
        assertThat(actual).isSameAs(legal);
    }

    private void verifyGivenCompletePfDataWhenTheNaturalStrategyBuildsThenANaturalPersonWithTheCpfIsReturned(
            final Customer actual) {
        assertThat(actual).isInstanceOf(NaturalPerson.class);
        assertThat(actual.document()).isEqualTo(CPF);
        assertThat(actual.personType()).isEqualTo(PersonType.PF);
    }

    private void verifyGivenPfDataWithoutNameWhenTheNaturalStrategyBuildsThenClienteNomeIsRequired(
            final Throwable actual) {
        assertThat(actual).isInstanceOfSatisfying(FieldValidationException.class,
                e -> assertThat(e.field()).isEqualTo("cliente.nome"));
    }

    private void verifyGivenCompletePjDataWhenTheLegalStrategyBuildsThenALegalPersonWithTheCnpjIsReturned(
            final Customer actual) {
        assertThat(actual).isInstanceOf(LegalPerson.class);
        assertThat(actual.document()).isEqualTo(CNPJ);
        assertThat(actual.personType()).isEqualTo(PersonType.PJ);
    }

    private void verifyGivenPfDataSentForAPjCustomerWhenTheLegalStrategyBuildsThenClienteRazaoSocialIsRequired(
            final Throwable actual) {
        assertThat(actual).isInstanceOfSatisfying(FieldValidationException.class,
                e -> assertThat(e.field()).isEqualTo("cliente.razaoSocial"));
    }

    private void verifyGivenPjDataSentForAPfCustomerWhenTheNaturalStrategyBuildsThenAllFourMissingFieldsAreListed(
            final Throwable actual) {
        assertThat(actual).isInstanceOfSatisfying(FieldValidationException.class, e ->
                assertThat(e.violations()).extracting(Violation::field)
                        .containsExactly("cliente.nome", "cliente.dataNascimento", "cliente.sexo",
                                "cliente.estadoCivil"));
    }
}
