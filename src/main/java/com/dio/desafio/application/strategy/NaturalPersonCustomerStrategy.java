package com.dio.desafio.application.strategy;

import com.dio.desafio.application.exception.FieldValidationException;
import com.dio.desafio.application.exception.FieldValidationException.Violation;
import com.dio.desafio.application.port.in.ContractProposalCommand.CustomerData;
import com.dio.desafio.domain.proposal.address.Address;
import com.dio.desafio.domain.proposal.contact.Contact;
import com.dio.desafio.domain.proposal.person.Customer;
import com.dio.desafio.domain.proposal.person.NaturalPerson;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class NaturalPersonCustomerStrategy implements CustomerStrategy {

    private static final String REQUIRED = "é obrigatório para pessoa física (CPF)";

    @Override
    public Customer build(String document, CustomerData data, Address address, Contact contact) {
        List<Violation> violations = new ArrayList<>();
        if (data.name() == null || data.name().isBlank()) {
            violations.add(new Violation("cliente.nome", REQUIRED));
        }
        if (data.birthDate() == null) {
            violations.add(new Violation("cliente.dataNascimento", REQUIRED));
        }
        if (data.gender() == null) {
            violations.add(new Violation("cliente.sexo", REQUIRED));
        }
        if (data.civilStatus() == null) {
            violations.add(new Violation("cliente.estadoCivil", REQUIRED));
        }
        if (!violations.isEmpty()) {
            throw new FieldValidationException(violations);
        }
        return new NaturalPerson(data.name(), document, data.birthDate(), data.gender(), data.civilStatus(),
                address, contact);
    }
}
