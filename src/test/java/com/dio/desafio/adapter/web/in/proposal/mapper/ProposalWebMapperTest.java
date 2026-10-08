package com.dio.desafio.adapter.web.in.proposal.mapper;

import com.dio.desafio.adapter.web.in.proposal.dto.PropostaResponse;
import com.dio.desafio.domain.proposal.Proposal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.dio.desafio.support.TestConstants.INTEGER_ONE;
import static com.dio.desafio.support.TestConstants.INTEGER_THREE;
import static com.dio.desafio.support.domain.ProposalSupport.defaultProposal;
import static org.assertj.core.api.Assertions.assertThat;

class ProposalWebMapperTest {

    private final ProposalWebMapper mapper = new ProposalWebMapper();

    @Test
    @DisplayName("Given a proposal When mapped to a response Then Portuguese fields and installments are filled")
    void givenProposalWhenMappedToResponseThenPortugueseFieldsAndInstallmentsAreFilled() {
        final var proposal = defaultProposal().create();

        final var result = mapper.toResponse(proposal);

        verifyGivenProposalWhenMappedToResponseThenPortugueseFieldsAndInstallmentsAreFilled(result, proposal);
    }

    private void verifyGivenProposalWhenMappedToResponseThenPortugueseFieldsAndInstallmentsAreFilled(
            final PropostaResponse actual, final Proposal expected) {
        assertThat(actual.numero()).isEqualTo(expected.id());
        assertThat(actual.documento()).isEqualTo(expected.document());
        assertThat(actual.descricao()).isEqualTo(expected.productDescription());
        assertThat(actual.valor()).isEqualTo(expected.amount());
        assertThat(actual.prazoMeses()).isEqualTo(expected.termMonths());
        assertThat(actual.taxaMensal()).isEqualTo(expected.monthlyRate());
        assertThat(actual.status()).isEqualTo(expected.status());
        assertThat(actual.parcelas()).hasSize(INTEGER_THREE);
        assertThat(actual.parcelas().get(0).numero()).isEqualTo(INTEGER_ONE);
        assertThat(actual.parcelas().get(0).valor()).isEqualTo(expected.installments().get(0).amount());
        assertThat(actual.parcelas().get(0).saldoDevedor()).isEqualTo(expected.installments().get(0).balance());
    }
}
