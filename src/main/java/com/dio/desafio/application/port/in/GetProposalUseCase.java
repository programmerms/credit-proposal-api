package com.dio.desafio.application.port.in;

import com.dio.desafio.domain.proposal.Proposal;
import java.util.UUID;

/**
 * Inbound port for retrieving a previously generated proposal.
 */
public interface GetProposalUseCase {
    Proposal get(UUID id);
}
