package com.dio.desafio.adapter.persistence.repository;

import com.dio.desafio.adapter.persistence.entity.proposal.ProposalEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalJpaRepository extends JpaRepository<ProposalEntity, UUID> { }
