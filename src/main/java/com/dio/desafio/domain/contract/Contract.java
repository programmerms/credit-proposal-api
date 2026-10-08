package com.dio.desafio.domain.contract;

import com.dio.desafio.domain.proposal.person.Customer;
import java.time.Instant;
import java.util.UUID;

/**
 * Contracting of a proposal: links the proposal to the customer who accepted it.
 */
public record Contract(UUID id, UUID proposalId, Instant contractedAt, Customer customer) { }
