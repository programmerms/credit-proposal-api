package com.dio.desafio.adapter.web.in.contract.mapper;

import com.dio.desafio.adapter.web.in.contract.dto.ClienteDto;
import com.dio.desafio.adapter.web.in.contract.dto.ContatoDto;
import com.dio.desafio.adapter.web.in.contract.dto.ContratacaoResponse;
import com.dio.desafio.adapter.web.in.contract.dto.ContratarPropostaRequest;
import com.dio.desafio.adapter.web.in.contract.dto.EnderecoDto;
import com.dio.desafio.adapter.web.in.contract.dto.TelefoneDto;
import com.dio.desafio.application.exception.FieldValidationException;
import com.dio.desafio.application.exception.FieldValidationException.Violation;
import com.dio.desafio.application.port.in.ContractProposalCommand;
import com.dio.desafio.domain.proposal.person.enums.GenderEnum;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.dio.desafio.support.domain.ContractSupport.defaultContract;
import static com.dio.desafio.support.domain.ContractSupport.defaultLegalContract;
import static com.dio.desafio.support.dto.ClienteDtoSupport.defaultClienteDto;
import static com.dio.desafio.support.dto.ContatoDtoSupport.defaultContatoDto;
import static com.dio.desafio.support.dto.ContratarPropostaRequestSupport.defaultContratarPropostaRequest;
import static com.dio.desafio.support.dto.EnderecoDtoSupport.defaultEnderecoDto;
import static com.dio.desafio.support.dto.TelefoneDtoSupport.defaultTelefoneDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.instancio.Select.field;

class ContractWebMapperTest {

    private final ContractWebMapper mapper = new ContractWebMapper();

    @Test
    @DisplayName("Given a PF request When mapped to a command Then enums are parsed from Portuguese codes")
    void givenPfRequestWhenMappedToCommandThenEnumsAreParsedFromPortugueseCodes() {
        final var request = defaultContratarPropostaRequest().create();

        final var result = mapper.toCommand(request);

        verifyGivenPfRequestWhenMappedToCommandThenEnumsAreParsedFromPortugueseCodes(result, request);
    }

    @Test
    @DisplayName("Given an unknown gender code When mapped Then a field error on cliente.sexo is thrown")
    void givenUnknownGenderCodeWhenMappedThenFieldErrorOnClienteSexoIsThrown() {
        final var cliente = defaultClienteDto().set(field(ClienteDto::sexo), "XYZ").create();
        final var request = defaultContratarPropostaRequest()
                .set(field(ContratarPropostaRequest::cliente), cliente).create();

        final var thrown = catchThrowable(() -> mapper.toCommand(request));

        verifyGivenUnknownGenderCodeWhenMappedThenFieldErrorOnClienteSexoIsThrown(thrown);
    }

    @Test
    @DisplayName("Given an unknown phone type When mapped Then the field error points to the indexed phone")
    void givenUnknownPhoneTypeWhenMappedThenFieldErrorPointsToTheIndexedPhone() {
        final var telefone = defaultTelefoneDto().set(field(TelefoneDto::tipo), "FAX").create();
        final var contato = defaultContatoDto().set(field(ContatoDto::telefones), List.of(telefone)).create();
        final var request = defaultContratarPropostaRequest()
                .set(field(ContratarPropostaRequest::contato), contato).create();

        final var thrown = catchThrowable(() -> mapper.toCommand(request));

        verifyGivenUnknownPhoneTypeWhenMappedThenFieldErrorPointsToTheIndexedPhone(thrown);
    }

    @Test
    @DisplayName("Given several invalid enum codes When mapped Then every invalid field is reported together")
    void givenSeveralInvalidEnumCodesWhenMappedThenEveryInvalidFieldIsReportedTogether() {
        final var cliente = defaultClienteDto().set(field(ClienteDto::sexo), "XYZ")
                .set(field(ClienteDto::estadoCivil), "FOO").create();
        final var endereco = defaultEnderecoDto().set(field(EnderecoDto::tipo), "BAR").create();
        final var telefone = defaultTelefoneDto().set(field(TelefoneDto::tipo), "FAX").create();
        final var contato = defaultContatoDto().set(field(ContatoDto::telefones), List.of(telefone)).create();
        final var request = defaultContratarPropostaRequest()
                .set(field(ContratarPropostaRequest::cliente), cliente)
                .set(field(ContratarPropostaRequest::endereco), endereco)
                .set(field(ContratarPropostaRequest::contato), contato).create();

        final var thrown = catchThrowable(() -> mapper.toCommand(request));

        verifyGivenSeveralInvalidEnumCodesWhenMappedThenEveryInvalidFieldIsReportedTogether(thrown);
    }

    @Test
    @DisplayName("Given a PF contract When mapped to a response Then only the natural person fields are filled")
    void givenPfContractWhenMappedToResponseThenOnlyTheNaturalPersonFieldsAreFilled() {
        final var contract = defaultContract().create();

        final var result = mapper.toResponse(contract);

        verifyGivenPfContractWhenMappedToResponseThenOnlyTheNaturalPersonFieldsAreFilled(result);
    }

    @Test
    @DisplayName("Given a PJ contract When mapped to a response Then only the legal person fields are filled")
    void givenPjContractWhenMappedToResponseThenOnlyTheLegalPersonFieldsAreFilled() {
        final var contract = defaultLegalContract().create();

        final var result = mapper.toResponse(contract);

        verifyGivenPjContractWhenMappedToResponseThenOnlyTheLegalPersonFieldsAreFilled(result);
    }

    private void verifyGivenPfRequestWhenMappedToCommandThenEnumsAreParsedFromPortugueseCodes(
            final ContractProposalCommand actual, final ContratarPropostaRequest request) {
        assertThat(actual.proposalId()).isEqualTo(request.numeroProposta());
        assertThat(actual.customer().gender()).isEqualTo(GenderEnum.FEMALE);
        assertThat(actual.address().streetName()).isEqualTo("Rua A");
        assertThat(actual.contact().phones()).hasSize(1);
    }

    private void verifyGivenUnknownGenderCodeWhenMappedThenFieldErrorOnClienteSexoIsThrown(final Throwable actual) {
        assertThat(actual).isInstanceOfSatisfying(FieldValidationException.class,
                e -> assertThat(e.field()).isEqualTo("cliente.sexo"));
    }

    private void verifyGivenUnknownPhoneTypeWhenMappedThenFieldErrorPointsToTheIndexedPhone(final Throwable actual) {
        assertThat(actual).isInstanceOfSatisfying(FieldValidationException.class,
                e -> assertThat(e.field()).isEqualTo("contato.telefones[0].tipo"));
    }

    private void verifyGivenSeveralInvalidEnumCodesWhenMappedThenEveryInvalidFieldIsReportedTogether(
            final Throwable actual) {
        assertThat(actual).isInstanceOfSatisfying(FieldValidationException.class, e ->
                assertThat(e.violations()).extracting(Violation::field)
                        .containsExactly("cliente.sexo", "cliente.estadoCivil", "endereco.tipo",
                                "contato.telefones[0].tipo"));
    }

    private void verifyGivenPfContractWhenMappedToResponseThenOnlyTheNaturalPersonFieldsAreFilled(
            final ContratacaoResponse actual) {
        assertThat(actual.tipoPessoa()).isEqualTo("PF");
        assertThat(actual.cliente().nome()).isEqualTo("Maria Silva");
        assertThat(actual.cliente().sexo()).isEqualTo("FEMININO");
        assertThat(actual.cliente().razaoSocial()).isNull();
        assertThat(actual.endereco().tipo()).isEqualTo("RESIDENCIAL");
    }

    private void verifyGivenPjContractWhenMappedToResponseThenOnlyTheLegalPersonFieldsAreFilled(
            final ContratacaoResponse actual) {
        assertThat(actual.tipoPessoa()).isEqualTo("PJ");
        assertThat(actual.cliente().razaoSocial()).isEqualTo("Empresa Ltda");
        assertThat(actual.cliente().nome()).isNull();
    }
}
