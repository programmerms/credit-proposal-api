package com.dio.desafio.application.port.in;

import com.dio.desafio.domain.proposal.Proposal;
import java.math.BigDecimal;

/**
 * Inbound port for generating credit proposals.
 */
public interface GenerateProposalUseCase {
    Proposal generate(String document, String productCode, BigDecimal amount, int termMonths);
}
