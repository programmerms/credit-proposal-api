package com.dio.desafio.adapter.persistence.adapter;

import com.dio.desafio.adapter.persistence.entity.proposal.ProposalEntity;
import com.dio.desafio.adapter.persistence.repository.ProposalJpaRepository;
import com.dio.desafio.application.port.out.ProposalPersistencePort;
import com.dio.desafio.domain.proposal.Proposal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import static com.dio.desafio.support.TestConstants.INTEGER_THREE;
import static com.dio.desafio.support.domain.ProposalSupport.defaultProposal;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ProposalPersistenceIntegrationTest {

    private final ProposalPersistencePort port;
    private final ProposalJpaRepository repository;

    ProposalPersistenceIntegrationTest(final ProposalPersistencePort port, final ProposalJpaRepository repository) {
        this.port = port;
        this.repository = repository;
    }

    @Test
    @DisplayName("Given a proposal When saved in H2 Then it can be reloaded intact through the port")
    void givenProposalWhenSavedInH2ThenItCanBeReloadedIntactThroughThePort() {
        final var proposal = defaultProposal().create();
        setupGivenProposalWhenSavedInH2(proposal);

        final var result = port.findById(proposal.id());

        verifyGivenProposalWhenSavedInH2ThenItCanBeReloadedIntactThroughThePort(result, proposal);
    }

    @Test
    @DisplayName("Given a proposal When saved in H2 Then every column including the rate snapshot is stored")
    void givenProposalWhenSavedInH2ThenEveryColumnIncludingTheRateSnapshotIsStored() {
        final var proposal = defaultProposal().create();
        setupGivenProposalWhenSavedInH2(proposal);

        final var entity = repository.findById(proposal.id());

        verifyGivenProposalWhenSavedInH2ThenEveryColumnIncludingTheRateSnapshotIsStored(entity, proposal);
    }

    @Test
    @DisplayName("Given an unknown proposal id When findById is called Then an empty result is returned")
    void givenUnknownProposalIdWhenFindByIdIsCalledThenEmptyResultIsReturned() {
        final var unknownId = UUID.randomUUID();

        final var result = port.findById(unknownId);

        verifyGivenUnknownProposalIdWhenFindByIdIsCalledThenEmptyResultIsReturned(result);
    }

    private void setupGivenProposalWhenSavedInH2(final Proposal proposal) {
        port.save(proposal);
    }

    private void verifyGivenProposalWhenSavedInH2ThenItCanBeReloadedIntactThroughThePort(
            final Optional<Proposal> actual, final Proposal expected) {
        assertThat(actual).contains(expected);
    }

    private void verifyGivenProposalWhenSavedInH2ThenEveryColumnIncludingTheRateSnapshotIsStored(
            final Optional<ProposalEntity> actual, final Proposal expected) {
        assertThat(actual).hasValueSatisfying(entity -> {
            assertThat(entity.getId()).isEqualTo(expected.id());
            assertThat(entity.getDocument()).isEqualTo(expected.document());
            assertThat(entity.getPersonType()).isEqualTo(expected.personType());
            assertThat(entity.getProductCode()).isEqualTo(expected.productCode());
            assertThat(entity.getProductDescription()).isEqualTo(expected.productDescription());
            assertThat(entity.getMonthlyRate()).isEqualByComparingTo(expected.monthlyRate());
            assertThat(entity.getStatus()).isEqualTo(expected.status());
            assertThat(entity.getCreatedAt()).isEqualTo(expected.createdAt());
            assertThat(entity.getAmount()).isEqualByComparingTo(expected.amount());
            assertThat(entity.getTermMonths()).isEqualTo(expected.termMonths());
            assertThat(entity.getInstallments()).hasSize(INTEGER_THREE);
        });
    }

    private void verifyGivenUnknownProposalIdWhenFindByIdIsCalledThenEmptyResultIsReturned(
            final Optional<Proposal> actual) {
        assertThat(actual).isEmpty();
    }
}
