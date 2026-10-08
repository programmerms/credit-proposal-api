package com.dio.desafio.adapter.persistence.adapter;

import com.dio.desafio.adapter.persistence.mapper.proposal.ProposalMapper;
import com.dio.desafio.adapter.persistence.repository.ProposalJpaRepository;
import com.dio.desafio.application.port.out.ProposalPersistencePort;
import com.dio.desafio.domain.proposal.Proposal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProposalPersistenceAdapter implements ProposalPersistencePort {

    private final ProposalJpaRepository repository;
    private final ProposalMapper mapper;

    public ProposalPersistenceAdapter(ProposalJpaRepository repository, ProposalMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Proposal save(Proposal proposal) {
        try {
            return mapper.toDomain(repository.save(mapper.toEntity(proposal)));
        } catch (RuntimeException exception) {
            throw new IllegalStateException("proposal persistence failed", exception);
        }
    }

    @Override
    public Optional<Proposal> findById(UUID id) {
        try {
            return repository.findById(id).map(mapper::toDomain);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("proposal lookup failed", exception);
        }
    }
}
