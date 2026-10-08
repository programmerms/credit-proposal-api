package com.dio.desafio.adapter.persistence.adapter;

import com.dio.desafio.application.exception.DuplicateContractException;
import com.dio.desafio.application.port.out.ContractPersistencePort;
import com.dio.desafio.domain.contract.Contract;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import static com.dio.desafio.support.domain.ContractSupport.defaultContract;
import static com.dio.desafio.support.domain.ContractSupport.defaultLegalContract;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.instancio.Select.field;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ContractPersistenceIntegrationTest {

    private final ContractPersistencePort port;

    ContractPersistenceIntegrationTest(final ContractPersistencePort port) {
        this.port = port;
    }

    @Test
    @DisplayName("Given PF and PJ contracts When saved in H2 Then both can be reloaded intact")
    void givenPfAndPjContractsWhenSavedInH2ThenBothCanBeReloadedIntact() {
        final var pf = defaultContract().create();
        final var pj = defaultLegalContract().create();
        setupGivenPfAndPjContractsWhenSavedInH2ThenBothCanBeReloadedIntact(pf, pj);

        final var reloadedPf = port.findById(pf.id());
        final var reloadedPj = port.findById(pj.id());

        verifyGivenPfAndPjContractsWhenSavedInH2ThenBothCanBeReloadedIntact(reloadedPf, pf, reloadedPj, pj);
    }

    @Test
    @DisplayName("Given an unknown contract id When findById is called Then an empty result is returned")
    void givenUnknownContractIdWhenFindByIdIsCalledThenEmptyResultIsReturned() {
        final var unknownId = UUID.randomUUID();

        final var result = port.findById(unknownId);

        verifyGivenUnknownContractIdWhenFindByIdIsCalledThenEmptyResultIsReturned(result);
    }

    @Test
    @DisplayName("Given a proposal already contracted When a second contract is saved Then the unique constraint becomes a DuplicateContractException")
    void givenProposalAlreadyContractedWhenSecondContractIsSavedThenUniqueConstraintBecomesDuplicateContractException() {
        final var proposalId = UUID.randomUUID();
        final var second = defaultContract().set(field(Contract::proposalId), proposalId).create();
        setupGivenProposalAlreadyContractedWhenSecondContractIsSaved(proposalId);

        final var thrown = catchThrowable(() -> port.save(second));

        verifyGivenProposalAlreadyContractedWhenSecondContractIsSaved(thrown);
    }

    private void setupGivenPfAndPjContractsWhenSavedInH2ThenBothCanBeReloadedIntact(
            final Contract pf, final Contract pj) {
        port.save(pf);
        port.save(pj);
    }

    private void verifyGivenPfAndPjContractsWhenSavedInH2ThenBothCanBeReloadedIntact(
            final Optional<Contract> actualPf, final Contract expectedPf,
            final Optional<Contract> actualPj, final Contract expectedPj) {
        assertThat(actualPf).contains(expectedPf);
        assertThat(actualPj).contains(expectedPj);
    }

    private void verifyGivenUnknownContractIdWhenFindByIdIsCalledThenEmptyResultIsReturned(
            final Optional<Contract> actual) {
        assertThat(actual).isEmpty();
    }

    private void setupGivenProposalAlreadyContractedWhenSecondContractIsSaved(final UUID proposalId) {
        port.save(defaultContract().set(field(Contract::proposalId), proposalId).create());
    }

    private void verifyGivenProposalAlreadyContractedWhenSecondContractIsSaved(final Throwable actual) {
        assertThat(actual).isInstanceOf(DuplicateContractException.class);
    }
}
