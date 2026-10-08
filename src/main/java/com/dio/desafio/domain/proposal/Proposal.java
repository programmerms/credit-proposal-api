package com.dio.desafio.domain.proposal;

import com.dio.desafio.domain.DocumentIdentity;
import com.dio.desafio.domain.PersonType;
import com.dio.desafio.domain.Product;
import com.dio.desafio.domain.ProposalStatus;
import com.dio.desafio.domain.proposal.installment.Installment;
import com.dio.desafio.domain.proposal.installment.InstallmentPlanCalculator;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * A credit proposal. PF and PJ share this single model: the person type and the product code select the
 * catalog entry (and therefore the monthly rate), which is snapshotted here together with the installments.
 */
public record Proposal(UUID id, Instant createdAt, String document, PersonType personType,
                       String productCode, String productDescription, BigDecimal amount, int termMonths,
                       BigDecimal monthlyRate, ProposalStatus status, List<Installment> installments) {

    public Proposal {
        installments = List.copyOf(installments);
    }

    public static Proposal generate(UUID id, Instant createdAt, DocumentIdentity identity, Product product,
                                    BigDecimal amount, int termMonths) {
        LocalDate dateInitiated = LocalDate.ofInstant(createdAt, ZoneOffset.UTC);
        List<Installment> installments =
                InstallmentPlanCalculator.price(amount, product.monthlyRate(), termMonths, dateInitiated);
        return new Proposal(id, createdAt, identity.normalizedDocument(), identity.personType(), product.code(),
                product.description(), amount, termMonths, product.monthlyRate(), ProposalStatus.GERADA,
                installments);
    }

    public Proposal contract() {
        if (status != ProposalStatus.GERADA) {
            throw new IllegalStateException("only a GERADA proposal can be contracted");
        }
        return new Proposal(id, createdAt, document, personType, productCode, productDescription, amount,
                termMonths, monthlyRate, ProposalStatus.CONTRATADA, installments);
    }

    public LocalDate dateInitiated() {
        return LocalDate.ofInstant(createdAt, ZoneOffset.UTC);
    }

    public LocalDate dateFinal() {
        return installments.get(installments.size() - 1).dueDate();
    }
}

