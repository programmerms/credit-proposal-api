package com.dio.desafio.adapter.persistence.repository;

import com.dio.desafio.adapter.persistence.entity.contract.ContractEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractJpaRepository extends JpaRepository<ContractEntity, UUID> { }
