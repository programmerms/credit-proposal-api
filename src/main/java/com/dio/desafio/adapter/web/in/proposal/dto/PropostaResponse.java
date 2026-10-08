package com.dio.desafio.adapter.web.in.proposal.dto;

import com.dio.desafio.domain.ProposalStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PropostaResponse(
        UUID numero,
        String documento,
        String descricao,
        BigDecimal valor,
        int prazoMeses,
        BigDecimal taxaMensal,
        ProposalStatus status,
        List<ParcelaResponse> parcelas) {

    public record ParcelaResponse(
            int numero,
            LocalDate vencimento,
            BigDecimal amortizacao,
            BigDecimal juros,
            BigDecimal valor,
            BigDecimal saldoDevedor) { }
}
