package com.dio.desafio.domain.proposal.contact;

import com.dio.desafio.domain.proposal.contact.phone.Phone;
import java.util.List;

public record Contact(String email, List<Phone> phones) {

    public Contact {
        phones = phones == null ? List.of() : List.copyOf(phones);
    }
}
