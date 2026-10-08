package com.dio.desafio.application.service;

import com.dio.desafio.application.exception.ProposalException;
import com.dio.desafio.application.port.in.GetProposalUseCase;
import com.dio.desafio.application.port.out.ProposalPersistencePort;
import com.dio.desafio.domain.proposal.Proposal;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ProposalQueryService implements GetProposalUseCase {

    private final ProposalPersistencePort persistencePort;

    public ProposalQueryService(ProposalPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public Proposal get(UUID id) {
        return persistencePort.findById(id)
                .orElseThrow(() -> new ProposalException("PROPOSTA_NAO_ENCONTRADA", "Proposta não encontrada"));
    }
}
