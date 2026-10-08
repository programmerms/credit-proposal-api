package com.dio.desafio.application.service;

import com.dio.desafio.application.exception.FieldValidationException;
import com.dio.desafio.application.port.out.ProposalPersistencePort;
import com.dio.desafio.application.strategy.RateStrategy;
import com.dio.desafio.application.strategy.StrategySelector;
import com.dio.desafio.domain.DocumentValidator;
import com.dio.desafio.domain.PersonType;
import com.dio.desafio.domain.Product;
import com.dio.desafio.domain.ProposalStatus;
import com.dio.desafio.domain.proposal.Proposal;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.dio.desafio.support.TestConstants.CPF;
import static com.dio.desafio.support.TestConstants.INTEGER_ONE;
import static com.dio.desafio.support.domain.ProductSupport.defaultProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ProposalServiceTest {

    private static final String FORMATTED_CPF = "529.982.247-25";
    private static final String PRODUCT_CODE = "CREDITO_PESSOAL";

    private ProposalPersistencePort persistence;
    private StrategySelector selector;
    private RateStrategy strategy;
    private ProposalService service;

    @BeforeEach
    void setUp() {
        persistence = mock(ProposalPersistencePort.class);
        selector = mock(StrategySelector.class);
        strategy = mock(RateStrategy.class);
        service = new ProposalService(persistence, selector, new DocumentValidator());
    }

    @Test
    @DisplayName("Given valid PF data When the Service generates a proposal Then it normalizes, applies the rate and persists")
    void givenValidPfDataWhenTheServiceGeneratesAProposalThenItNormalizesAppliesTheRateAndPersists() {
        final var product = defaultProduct().create();
        setupGivenValidPfDataWhenTheServiceGeneratesAProposal(product);

        final var result = service.generate(FORMATTED_CPF, PRODUCT_CODE, new BigDecimal("100"), 12);

        verifyGivenValidPfDataWhenTheServiceGeneratesAProposal(result, product);
    }

    @Test
    @DisplayName("Given a zero amount When the Service generates Then it reports field validation on valor")
    void givenZeroAmountWhenTheServiceGeneratesThenItReportsFieldValidationOnValor() {
        final var thrown = catchThrowable(() -> service.generate(CPF, PRODUCT_CODE, BigDecimal.ZERO, 12));

        verifyGivenZeroAmountWhenTheServiceGeneratesThenItReportsFieldValidationOnValor(thrown);
    }

    @Test
    @DisplayName("Given a zero term When the Service generates Then it reports field validation on prazoMeses")
    void givenZeroTermWhenTheServiceGeneratesThenItReportsFieldValidationOnPrazoMeses() {
        final var thrown = catchThrowable(() -> service.generate(CPF, PRODUCT_CODE, BigDecimal.ONE, 0));

        verifyGivenZeroTermWhenTheServiceGeneratesThenItReportsFieldValidationOnPrazoMeses(thrown);
    }

    @Test
    @DisplayName("Given an invalid document When the Service generates Then it reports field validation on documento")
    void givenInvalidDocumentWhenTheServiceGeneratesThenItReportsFieldValidationOnDocumento() {
        final var thrown = catchThrowable(() -> service.generate("111.111.111-11", PRODUCT_CODE, BigDecimal.ONE, 12));

        verifyGivenInvalidDocumentWhenTheServiceGeneratesThenItReportsFieldValidationOnDocumento(thrown);
    }

    private void setupGivenValidPfDataWhenTheServiceGeneratesAProposal(final Product product) {
        doReturn(strategy).when(selector).select(PersonType.PF);
        doReturn(product).when(strategy).resolve(PRODUCT_CODE);
        doAnswer(invocation -> invocation.getArgument(0)).when(persistence).save(any(Proposal.class));
    }

    private void verifyGivenValidPfDataWhenTheServiceGeneratesAProposal(
            final Proposal actual, final Product product) {
        assertThat(actual.document()).isEqualTo(CPF);
        assertThat(actual.personType()).isEqualTo(PersonType.PF);
        assertThat(actual.monthlyRate()).isEqualByComparingTo(product.monthlyRate());
        assertThat(actual.status()).isEqualTo(ProposalStatus.GERADA);
        assertThat(actual.installments()).hasSize(12);
        assertThat(actual.dateFinal()).isEqualTo(actual.dateInitiated().plusMonths(12));
        verify(persistence, times(INTEGER_ONE)).save(any(Proposal.class));
    }

    private void verifyGivenZeroAmountWhenTheServiceGeneratesThenItReportsFieldValidationOnValor(final Throwable actual) {
        assertFieldValidation(actual, "valor");
    }

    private void verifyGivenZeroTermWhenTheServiceGeneratesThenItReportsFieldValidationOnPrazoMeses(final Throwable actual) {
        assertFieldValidation(actual, "prazoMeses");
    }

    private void verifyGivenInvalidDocumentWhenTheServiceGeneratesThenItReportsFieldValidationOnDocumento(final Throwable actual) {
        assertFieldValidation(actual, "documento");
    }

    private void assertFieldValidation(final Throwable actual, final String expectedField) {
        assertThat(actual).isInstanceOfSatisfying(FieldValidationException.class,
                e -> assertThat(e.field()).isEqualTo(expectedField));
        verify(persistence, never()).save(any(Proposal.class));
    }
}
