package com.dio.desafio.domain.proposal.person;

import com.dio.desafio.domain.PersonType;
import com.dio.desafio.domain.proposal.address.Address;
import com.dio.desafio.domain.proposal.contact.Contact;
import com.dio.desafio.domain.proposal.person.enums.CivilStatusEnum;
import com.dio.desafio.domain.proposal.person.enums.GenderEnum;
import java.time.LocalDate;

public record NaturalPerson(String name, String cpfNumber, LocalDate birthDate, GenderEnum gender,
                            CivilStatusEnum civilStatus, Address address, Contact contact) implements Customer {

    @Override
    public PersonType personType() {
        return PersonType.PF;
    }

    @Override
    public String document() {
        return cpfNumber;
    }
}
