package com.dio.desafio.support.domain;

import com.dio.desafio.domain.proposal.person.LegalPerson;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static com.dio.desafio.support.TestConstants.CNPJ;
import static com.dio.desafio.support.domain.AddressSupport.defaultAddress;
import static com.dio.desafio.support.domain.ContactSupport.defaultContact;
import static org.instancio.Select.field;

public final class LegalPersonSupport {

    private LegalPersonSupport() { }

    public static InstancioApi<LegalPerson> defaultLegalPerson() {
        return Instancio.of(LegalPerson.class)
                .set(field(LegalPerson::cnpjNumber), CNPJ)
                .set(field(LegalPerson::businessName), "Empresa Ltda")
                .set(field(LegalPerson::tradeName), "Empresa")
                .supply(field(LegalPerson::address), () -> defaultAddress().create())
                .supply(field(LegalPerson::contact), () -> defaultContact().create());
    }
}
