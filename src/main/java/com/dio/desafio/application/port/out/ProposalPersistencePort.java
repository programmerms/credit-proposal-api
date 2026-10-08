package com.dio.desafio.application.port.out;

import com.dio.desafio.domain.proposal.Proposal;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for persisting and reading proposals.
 */
public interface ProposalPersistencePort {
    Proposal save(Proposal proposal);

    Optional<Proposal> findById(UUID id);
}
