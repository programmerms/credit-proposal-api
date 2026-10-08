package com.dio.desafio.domain.proposal.person;

import com.dio.desafio.domain.PersonType;
import com.dio.desafio.domain.proposal.address.Address;
import com.dio.desafio.domain.proposal.contact.Contact;

public record LegalPerson(String cnpjNumber, String businessName, String tradeName, Address address,
                          Contact contact) implements Customer {

    @Override
    public PersonType personType() {
        return PersonType.PJ;
    }

    @Override
    public String document() {
        return cnpjNumber;
    }
}
