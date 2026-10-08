package com.dio.desafio.adapter.persistence.mapper.proposal;

import com.dio.desafio.adapter.persistence.entity.proposal.InstallmentEmbeddable;
import com.dio.desafio.adapter.persistence.entity.proposal.ProposalEntity;
import com.dio.desafio.domain.proposal.Proposal;
import com.dio.desafio.domain.proposal.installment.Installment;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class ProposalMapper {

    public ProposalEntity toEntity(Proposal proposal) {
        List<InstallmentEmbeddable> installments = proposal.installments().stream()
                .map(i -> new InstallmentEmbeddable(i.number(), i.dueDate(), i.principal(),
                        i.interest(), i.amount(), i.balance()))
                .toList();
        return new ProposalEntity(proposal.id(), proposal.createdAt(), proposal.document(),
                proposal.personType(), proposal.productCode(), proposal.productDescription(),
                proposal.amount(), proposal.termMonths(), proposal.monthlyRate(), proposal.status(),
                installments);
    }

    public Proposal toDomain(ProposalEntity entity) {
        List<Installment> installments = entity.getInstallments().stream()
                .map(i -> new Installment(i.getNumber(), i.getDueDate(), i.getPrincipal(),
                        i.getInterest(), i.getAmount(), i.getBalance()))
                .toList();
        return new Proposal(entity.getId(), entity.getCreatedAt(), entity.getDocument(),
                entity.getPersonType(), entity.getProductCode(), entity.getProductDescription(),
                entity.getAmount(), entity.getTermMonths(), entity.getMonthlyRate(),
                entity.getStatus(), installments);
    }
}
