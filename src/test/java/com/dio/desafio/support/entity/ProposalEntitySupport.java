package com.dio.desafio.support.entity;

import com.dio.desafio.adapter.persistence.entity.proposal.ProposalEntity;
import org.instancio.Instancio;
import org.instancio.InstancioApi;

public final class ProposalEntitySupport {

    private ProposalEntitySupport() { }

    public static InstancioApi<ProposalEntity> defaultProposalEntity() {
        return Instancio.of(ProposalEntity.class);
    }
}
