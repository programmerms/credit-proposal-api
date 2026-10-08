package com.dio.desafio.adapter.persistence.mapper.proposal;

import com.dio.desafio.domain.proposal.Proposal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.dio.desafio.support.domain.ProposalSupport.defaultProposal;
import static org.assertj.core.api.Assertions.assertThat;

class ProposalMapperTest {

    private final ProposalMapper mapper = new ProposalMapper();

    @Test
    @DisplayName("Given a proposal When mapped to persistence and back Then every field is retained")
    void givenProposalWhenMappedToPersistenceAndBackThenEveryFieldIsRetained() {
        final var expected = defaultProposal().create();

        final var actual = mapper.toDomain(mapper.toEntity(expected));

        verifyGivenProposalWhenMappedToPersistenceAndBackThenEveryFieldIsRetained(actual, expected);
    }

    private void verifyGivenProposalWhenMappedToPersistenceAndBackThenEveryFieldIsRetained(
            final Proposal actual, final Proposal expected) {
        assertThat(actual).isEqualTo(expected);
    }
}
