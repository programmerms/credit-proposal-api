package com.dio.desafio.application.service;

import com.dio.desafio.application.exception.DuplicateContractException;
import com.dio.desafio.application.exception.ProposalException;
import com.dio.desafio.application.port.in.ContractProposalCommand;
import com.dio.desafio.application.port.in.ContractProposalUseCase;
import com.dio.desafio.application.port.in.GetContractUseCase;
import com.dio.desafio.application.port.out.ContractPersistencePort;
import com.dio.desafio.application.port.out.ProposalPersistencePort;
import com.dio.desafio.application.strategy.CustomerStrategySelector;
import com.dio.desafio.domain.ProposalStatus;
import com.dio.desafio.domain.contract.Contract;
import com.dio.desafio.domain.proposal.Proposal;
import com.dio.desafio.domain.proposal.person.Customer;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade of the contracting phase: finds the proposal, builds the customer through the
 * PF/PJ Strategy, persists the contract and moves the proposal to {@code CONTRATADA}.
 */
@Service
public class ContractService implements ContractProposalUseCase, GetContractUseCase {

    private final ProposalPersistencePort proposalPort;
    private final ContractPersistencePort contractPort;
    private final CustomerStrategySelector customerStrategySelector;

    public ContractService(ProposalPersistencePort proposalPort, ContractPersistencePort contractPort,
                           CustomerStrategySelector customerStrategySelector) {
        this.proposalPort = proposalPort;
        this.contractPort = contractPort;
        this.customerStrategySelector = customerStrategySelector;
    }

    @Override
    @Transactional
    public Contract contract(ContractProposalCommand command) {
        Proposal proposal = proposalPort.findById(command.proposalId())
                .orElseThrow(() -> new ProposalException("PROPOSTA_NAO_ENCONTRADA",
                        "Proposta não encontrada"));
        if (proposal.status() == ProposalStatus.CONTRATADA) {
            throw alreadyContracted();
        }
        Customer customer = customerStrategySelector.select(proposal.personType())
                .build(proposal.document(), command.customer(), command.address(), command.contact());
        Contract saved;
        try {
            saved = contractPort.save(new Contract(UUID.randomUUID(), proposal.id(), Instant.now(), customer));
        } catch (DuplicateContractException exception) {
            throw alreadyContracted();
        }
        proposalPort.save(proposal.contract());
        return saved;
    }

    @Override
    public Contract get(UUID id) {
        return contractPort.findById(id)
                .orElseThrow(() -> new ProposalException("CONTRATACAO_NAO_ENCONTRADA",
                        "Contratação não encontrada"));
    }

    static ProposalException alreadyContracted() {
        return new ProposalException("PROPOSTA_JA_CONTRATADA", "A proposta já foi contratada");
    }
}
