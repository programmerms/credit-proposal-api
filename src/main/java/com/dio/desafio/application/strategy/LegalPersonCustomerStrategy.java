package com.dio.desafio.application.strategy;

import com.dio.desafio.application.exception.FieldValidationException;
import com.dio.desafio.application.port.in.ContractProposalCommand.CustomerData;
import com.dio.desafio.domain.proposal.address.Address;
import com.dio.desafio.domain.proposal.contact.Contact;
import com.dio.desafio.domain.proposal.person.Customer;
import com.dio.desafio.domain.proposal.person.LegalPerson;
import org.springframework.stereotype.Component;

@Component
public class LegalPersonCustomerStrategy implements CustomerStrategy {

    @Override
    public Customer build(String document, CustomerData data, Address address, Contact contact) {
        if (data.businessName() == null || data.businessName().isBlank()) {
            throw new FieldValidationException("cliente.razaoSocial",
                    "é obrigatório para pessoa jurídica (CNPJ)");
        }
        return new LegalPerson(document, data.businessName(), data.tradeName(), address, contact);
    }
}
