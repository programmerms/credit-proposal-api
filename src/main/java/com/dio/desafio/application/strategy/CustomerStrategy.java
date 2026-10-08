package com.dio.desafio.application.strategy;

import com.dio.desafio.application.port.in.ContractProposalCommand.CustomerData;
import com.dio.desafio.domain.proposal.address.Address;
import com.dio.desafio.domain.proposal.contact.Contact;
import com.dio.desafio.domain.proposal.person.Customer;

/**
 * Strategy that validates the person-specific data and builds the customer for one person type.
 */
public interface CustomerStrategy {
    Customer build(String document, CustomerData data, Address address, Contact contact);
}
