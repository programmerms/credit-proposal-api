package com.dio.desafio.domain.proposal.person;

import com.dio.desafio.domain.PersonType;
import com.dio.desafio.domain.proposal.address.Address;
import com.dio.desafio.domain.proposal.contact.Contact;

/**
 * Customer that contracts a proposal: a natural person (CPF) or a legal person (CNPJ).
 */
public sealed interface Customer permits NaturalPerson, LegalPerson {

    PersonType personType();

    String document();

    Address address();

    Contact contact();
}
