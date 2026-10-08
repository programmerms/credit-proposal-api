package com.dio.desafio.adapter.web.exception;

import com.dio.desafio.application.exception.FieldValidationException;
import com.dio.desafio.application.exception.FieldValidationException.Violation;
import com.dio.desafio.application.exception.ProposalException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Given invalid document data When handled Then response contains common and field error details")
    void givenInvalidDocumentDataWhenHandledThenResponseContainsCommonAndFieldErrorDetails() {
        final var exception = new FieldValidationException("documento", "CPF inválido");

        final var result = handler.handleFieldValidation(exception);

        verifyGivenInvalidDocumentDataWhenHandledThenResponseContainsCommonAndFieldErrorDetails(result);
    }

    @Test
    @DisplayName("Given several violations When handled Then every field error is listed")
    void givenSeveralViolationsWhenHandledThenEveryFieldErrorIsListed() {
        final var exception = new FieldValidationException(List.of(
                new Violation("cliente.nome", "é obrigatório"), new Violation("cliente.sexo", "é obrigatório")));

        final var result = handler.handleFieldValidation(exception);

        verifyGivenSeveralViolationsWhenHandledThenEveryFieldErrorIsListed(result);
    }

    @Test
    @DisplayName("Given a catalog failure When handled Then response has a safe centralized payload")
    void givenCatalogFailureWhenHandledThenResponseHasASafeCentralizedPayload() {
        final var exception = new ProposalException("CATALOGO_INDISPONIVEL",
                "Catálogo de produtos PF indisponível ou inválido");

        final var result = handler.handleProposal(exception);

        verifyGivenCatalogFailureWhenHandledThenResponseHasASafeCentralizedPayload(result);
    }

    @ParameterizedTest
    @CsvSource({
            "PROPOSTA_NAO_ENCONTRADA,404", "CONTRATACAO_NAO_ENCONTRADA,404", "PRODUTO_NAO_ENCONTRADO,404",
            "PROPOSTA_JA_CONTRATADA,409", "PRODUTO_INCOMPATIVEL,422", "CATALOGO_INDISPONIVEL,503",
            "CODIGO_DESCONHECIDO,500"})
    @DisplayName("Given a business code When handled Then it maps to the expected HTTP status")
    void givenBusinessCodeWhenHandledThenItMapsToTheExpectedHttpStatus(final String code, final int expectedStatus) {
        final var exception = new ProposalException(code, "mensagem");

        final var result = handler.handleProposal(exception);

        verifyGivenBusinessCodeWhenHandledThenItMapsToTheExpectedHttpStatus(result, expectedStatus);
    }

    private void verifyGivenInvalidDocumentDataWhenHandledThenResponseContainsCommonAndFieldErrorDetails(
            final ResponseEntity<ApiErrorResponse> actual) {
        assertThat(actual.getStatusCode().value()).isEqualTo(400);
        assertThat(actual.getBody().dataHora()).isNotNull();
        assertThat(actual.getBody().status()).isEqualTo(400);
        assertThat(actual.getBody().mensagem()).isEqualTo("Falha na validação da requisição");
        assertThat(actual.getBody().errosCampos())
                .containsExactly(new FieldErrorResponse("documento", "CPF inválido"));
    }

    private void verifyGivenSeveralViolationsWhenHandledThenEveryFieldErrorIsListed(
            final ResponseEntity<ApiErrorResponse> actual) {
        assertThat(actual.getBody().errosCampos()).extracting(FieldErrorResponse::campo)
                .containsExactly("cliente.nome", "cliente.sexo");
    }

    private void verifyGivenCatalogFailureWhenHandledThenResponseHasASafeCentralizedPayload(
            final ResponseEntity<ApiErrorResponse> actual) {
        assertThat(actual.getBody().status()).isEqualTo(503);
        assertThat(actual.getBody().mensagem()).contains("PF");
        assertThat(actual.getBody().errosCampos()).isNull();
    }

    private void verifyGivenBusinessCodeWhenHandledThenItMapsToTheExpectedHttpStatus(
            final ResponseEntity<ApiErrorResponse> actual, final int expectedStatus) {
        assertThat(actual.getStatusCode().value()).isEqualTo(expectedStatus);
        assertThat(actual.getBody().status()).isEqualTo(expectedStatus);
    }
}
