package com.dio.desafio.domain.proposal;

import com.dio.desafio.domain.ProposalStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.dio.desafio.support.domain.ProposalSupport.defaultProposal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.instancio.Select.field;

class ProposalTest {

    @Test
    @DisplayName("Given a GERADA proposal When contract is called Then it becomes CONTRATADA keeping its data")
    void givenAGeradaProposalWhenContractIsCalledThenItBecomesContratadaKeepingItsData() {
        final var proposal = defaultProposal().create();

        final var result = proposal.contract();

        verifyGivenAGeradaProposalWhenContractIsCalledThenItBecomesContratadaKeepingItsData(proposal, result);
    }

    @Test
    @DisplayName("Given a CONTRATADA proposal When contract is called again Then it is rejected")
    void givenAContratadaProposalWhenContractIsCalledAgainThenItIsRejected() {
        final var contracted = defaultProposal().set(field(Proposal::status), ProposalStatus.CONTRATADA).create();

        final var thrown = catchThrowable(contracted::contract);

        verifyGivenAContratadaProposalWhenContractIsCalledAgainThenItIsRejected(thrown);
    }

    private void verifyGivenAGeradaProposalWhenContractIsCalledThenItBecomesContratadaKeepingItsData(
            final Proposal original, final Proposal actual) {
        assertThat(original.status()).isEqualTo(ProposalStatus.GERADA);
        assertThat(actual.status()).isEqualTo(ProposalStatus.CONTRATADA);
        assertThat(actual).usingRecursiveComparison().ignoringFields("status").isEqualTo(original);
    }

    private void verifyGivenAContratadaProposalWhenContractIsCalledAgainThenItIsRejected(final Throwable actual) {
        assertThat(actual).isInstanceOf(IllegalStateException.class);
    }
}
