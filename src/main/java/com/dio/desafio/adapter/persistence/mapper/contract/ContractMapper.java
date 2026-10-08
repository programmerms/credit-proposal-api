package com.dio.desafio.adapter.persistence.mapper.contract;

import com.dio.desafio.adapter.persistence.entity.contract.AddressEmbeddable;
import com.dio.desafio.adapter.persistence.entity.contract.ContractEntity;
import com.dio.desafio.adapter.persistence.entity.contract.PhoneEmbeddable;
import com.dio.desafio.domain.contract.Contract;
import com.dio.desafio.domain.proposal.address.Address;
import com.dio.desafio.domain.proposal.contact.Contact;
import com.dio.desafio.domain.proposal.contact.phone.Phone;
import com.dio.desafio.domain.proposal.person.Customer;
import com.dio.desafio.domain.proposal.person.LegalPerson;
import com.dio.desafio.domain.proposal.person.NaturalPerson;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class ContractMapper {

    public ContractEntity toEntity(Contract contract) {
        Customer customer = contract.customer();
        Address address = customer.address();
        AddressEmbeddable addressEmbeddable = new AddressEmbeddable(address.type(), address.streetName(),
                address.streetNumber(), address.postalCode(), address.city(), address.state());
        List<PhoneEmbeddable> phones = customer.contact().phones().stream()
                .map(p -> new PhoneEmbeddable(p.type(), p.areaCode(), p.number())).toList();
        String email = customer.contact().email();
        if (customer instanceof NaturalPerson pf) {
            return new ContractEntity(contract.id(), contract.proposalId(), contract.contractedAt(),
                    pf.personType(), pf.cpfNumber(), pf.name(), pf.birthDate(), pf.gender(), pf.civilStatus(),
                    null, null, addressEmbeddable, email, phones);
        }
        LegalPerson pj = (LegalPerson) customer;
        return new ContractEntity(contract.id(), contract.proposalId(), contract.contractedAt(),
                pj.personType(), pj.cnpjNumber(), null, null, null, null,
                pj.businessName(), pj.tradeName(), addressEmbeddable, email, phones);
    }

    public Contract toDomain(ContractEntity entity) {
        AddressEmbeddable a = entity.getAddress();
        Address address = new Address(a.getType(), a.getStreetName(), a.getStreetNumber(), a.getPostalCode(),
                a.getCity(), a.getState());
        Contact contact = new Contact(entity.getEmail(), entity.getPhones().stream()
                .map(p -> new Phone(p.getType(), p.getAreaCode(), p.getNumber())).toList());
        Customer customer = switch (entity.getPersonType()) {
            case PF -> new NaturalPerson(entity.getName(), entity.getDocument(), entity.getBirthDate(),
                    entity.getGender(), entity.getCivilStatus(), address, contact);
            case PJ -> new LegalPerson(entity.getDocument(), entity.getBusinessName(), entity.getTradeName(),
                    address, contact);
        };
        return new Contract(entity.getId(), entity.getProposalId(), entity.getContractedAt(), customer);
    }
}
