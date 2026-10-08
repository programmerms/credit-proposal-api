package com.dio.desafio.application.port.in;

import com.dio.desafio.domain.contract.Contract;

/**
 * Inbound port for contracting a previously generated proposal.
 */
public interface ContractProposalUseCase {
    Contract contract(ContractProposalCommand command);
}
