package com.dio.desafio.support.entity;

import com.dio.desafio.adapter.persistence.entity.contract.ContractEntity;
import org.instancio.Instancio;
import org.instancio.InstancioApi;

public final class ContractEntitySupport {

    private ContractEntitySupport() { }

    public static InstancioApi<ContractEntity> defaultContractEntity() {
        return Instancio.of(ContractEntity.class);
    }
}
