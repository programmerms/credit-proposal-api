package com.dio.desafio.application.service;

import com.dio.desafio.application.exception.FieldValidationException;
import com.dio.desafio.application.port.in.GenerateProposalUseCase;
import com.dio.desafio.application.port.out.ProposalPersistencePort;
import com.dio.desafio.application.strategy.StrategySelector;
import com.dio.desafio.domain.DocumentIdentity;
import com.dio.desafio.domain.DocumentValidator;
import com.dio.desafio.domain.proposal.Proposal;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ProposalService implements GenerateProposalUseCase {

    private final DocumentValidator documentValidator;
    private final StrategySelector strategySelector;
    private final ProposalPersistencePort persistencePort;

    public ProposalService(ProposalPersistencePort persistencePort, StrategySelector strategySelector,
                           DocumentValidator documentValidator) {
        this.persistencePort = persistencePort;
        this.strategySelector = strategySelector;
        this.documentValidator = documentValidator;
    }

    @Override
    public Proposal generate(String document, String productCode, BigDecimal amount, int termMonths) {
        if (amount == null || amount.signum() <= 0) {
            throw new FieldValidationException("valor", "deve ser maior que zero");
        }
        if (termMonths <= 0) {
            throw new FieldValidationException("prazoMeses", "deve ser maior que zero");
        }
        DocumentIdentity identity;
        try {
            identity = documentValidator.identify(document);
        } catch (IllegalArgumentException exception) {
            throw new FieldValidationException("documento", exception.getMessage());
        }
        var product = strategySelector.select(identity.personType()).resolve(productCode);
        Proposal proposal = Proposal.generate(UUID.randomUUID(), Instant.now(), identity, product, amount, termMonths);
        return persistencePort.save(proposal);
    }
}
