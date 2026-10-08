package com.dio.desafio.support.domain;

import com.dio.desafio.domain.contract.Contract;
import java.time.Instant;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static com.dio.desafio.support.domain.LegalPersonSupport.defaultLegalPerson;
import static com.dio.desafio.support.domain.NaturalPersonSupport.defaultNaturalPerson;
import static org.instancio.Select.field;

public final class ContractSupport {

    private ContractSupport() { }

    /** Contract of a natural person (PF). */
    public static InstancioApi<Contract> defaultContract() {
        return Instancio.of(Contract.class)
                .set(field(Contract::contractedAt), Instant.parse("2026-02-01T10:00:00Z"))
                .supply(field(Contract::customer), () -> defaultNaturalPerson().create());
    }

    /** Contract of a legal person (PJ). */
    public static InstancioApi<Contract> defaultLegalContract() {
        return Instancio.of(Contract.class)
                .set(field(Contract::contractedAt), Instant.parse("2026-02-01T10:00:00Z"))
                .supply(field(Contract::customer), () -> defaultLegalPerson().create());
    }
}
