package com.dio.desafio.adapter.web.in.proposal.mapper;

import com.dio.desafio.adapter.web.in.proposal.dto.PropostaResponse.ParcelaResponse;
import com.dio.desafio.adapter.web.in.proposal.dto.PropostaResponse;
import com.dio.desafio.domain.proposal.Proposal;
import com.dio.desafio.domain.proposal.installment.Installment;
import org.springframework.stereotype.Component;

/**
 * Converts the proposal domain model into the Portuguese JSON contract of the proposal endpoints.
 */
@Component
public class ProposalWebMapper {

    public PropostaResponse toResponse(Proposal proposal) {
        return new PropostaResponse(
                proposal.id(),
                proposal.document(),
                proposal.productDescription(),
                proposal.amount(),
                proposal.termMonths(),
                proposal.monthlyRate(),
                proposal.status(),
                proposal.installments().stream().map(this::toParcela).toList());
    }

    private ParcelaResponse toParcela(Installment installment) {
        return new ParcelaResponse(
                installment.number(),
                installment.dueDate(),
                installment.principal(),
                installment.interest(),
                installment.amount(),
                installment.balance());
    }
}
