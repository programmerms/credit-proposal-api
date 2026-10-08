package com.dio.desafio.support.domain;

import com.dio.desafio.domain.PersonType;
import com.dio.desafio.domain.ProposalStatus;
import com.dio.desafio.domain.proposal.Proposal;
import com.dio.desafio.domain.proposal.installment.Installment;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.stream.IntStream;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static com.dio.desafio.support.TestConstants.CPF;
import static com.dio.desafio.support.TestConstants.INTEGER_THREE;
import static com.dio.desafio.support.domain.InstallmentSupport.defaultInstallment;
import static org.instancio.Select.field;

public final class ProposalSupport {

    private ProposalSupport() { }

    /** GERADA PF proposal with three installments. */
    public static InstancioApi<Proposal> defaultProposal() {
        return Instancio.of(Proposal.class)
                .set(field(Proposal::createdAt), Instant.parse("2026-01-01T12:00:00Z"))
                .set(field(Proposal::document), CPF)
                .set(field(Proposal::personType), PersonType.PF)
                .set(field(Proposal::productCode), "CREDITO_PESSOAL")
                .set(field(Proposal::productDescription), "Crédito Pessoal")
                .set(field(Proposal::amount), new BigDecimal("100.00"))
                .set(field(Proposal::termMonths), INTEGER_THREE)
                .set(field(Proposal::monthlyRate), new BigDecimal("0.05"))
                .set(field(Proposal::status), ProposalStatus.GERADA)
                .supply(field(Proposal::installments), () -> IntStream.rangeClosed(1, INTEGER_THREE)
                        .mapToObj(number -> defaultInstallment().set(field(Installment::number), number).create())
                        .toList());
    }
}
