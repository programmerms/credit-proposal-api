package com.dio.desafio.adapter.persistence.adapter;

import com.dio.desafio.adapter.persistence.mapper.contract.ContractMapper;
import com.dio.desafio.adapter.persistence.repository.ContractJpaRepository;
import com.dio.desafio.application.exception.DuplicateContractException;
import com.dio.desafio.application.port.out.ContractPersistencePort;
import com.dio.desafio.domain.contract.Contract;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class ContractPersistenceAdapter implements ContractPersistencePort {

    private final ContractJpaRepository repository;
    private final ContractMapper mapper;

    public ContractPersistenceAdapter(ContractJpaRepository repository, ContractMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Contract save(Contract contract) {
        try {
            return mapper.toDomain(repository.saveAndFlush(mapper.toEntity(contract)));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateContractException(exception);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("contract persistence failed", exception);
        }
    }

    @Override
    public Optional<Contract> findById(UUID id) {
        try {
            return repository.findById(id).map(mapper::toDomain);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("contract lookup failed", exception);
        }
    }
}
