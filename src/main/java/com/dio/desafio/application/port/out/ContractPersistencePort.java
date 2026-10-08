package com.dio.desafio.application.port.out;

import com.dio.desafio.domain.contract.Contract;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for persisting and reading contracts.
 */
public interface ContractPersistencePort {
    /**
     * @throws com.dio.desafio.application.exception.DuplicateContractException if the proposal already has a contract
     */
    Contract save(Contract contract);

    Optional<Contract> findById(UUID id);
}
