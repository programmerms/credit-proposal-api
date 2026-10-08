package com.dio.desafio.application.port.in;

import com.dio.desafio.domain.proposal.address.Address;
import com.dio.desafio.domain.proposal.contact.Contact;
import com.dio.desafio.domain.proposal.person.enums.CivilStatusEnum;
import com.dio.desafio.domain.proposal.person.enums.GenderEnum;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Input of the contracting use case. {@link CustomerData} carries the person-specific fields of both
 * PF and PJ; the Strategy selected by the proposal's person type decides which ones are required.
 */
public record ContractProposalCommand(UUID proposalId, CustomerData customer,
                                      Address address, Contact contact) {

    public record CustomerData(String name, LocalDate birthDate, GenderEnum gender,
                               CivilStatusEnum civilStatus, String businessName, String tradeName) { }
}
