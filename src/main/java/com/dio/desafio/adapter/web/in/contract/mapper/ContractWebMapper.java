package com.dio.desafio.adapter.web.in.contract.mapper;

import com.dio.desafio.adapter.web.in.contract.dto.ClienteDto;
import com.dio.desafio.adapter.web.in.contract.dto.ContatoDto;
import com.dio.desafio.adapter.web.in.contract.dto.ContratacaoResponse;
import com.dio.desafio.adapter.web.in.contract.dto.ContratarPropostaRequest;
import com.dio.desafio.adapter.web.in.contract.dto.EnderecoDto;
import com.dio.desafio.adapter.web.in.contract.dto.TelefoneDto;
import com.dio.desafio.application.exception.FieldValidationException.Violation;
import com.dio.desafio.application.exception.FieldValidationException;
import com.dio.desafio.application.port.in.ContractProposalCommand.CustomerData;
import com.dio.desafio.application.port.in.ContractProposalCommand;
import com.dio.desafio.domain.contract.Contract;
import com.dio.desafio.domain.proposal.address.Address;
import com.dio.desafio.domain.proposal.address.enums.AddressTypeEnum;
import com.dio.desafio.domain.proposal.contact.Contact;
import com.dio.desafio.domain.proposal.contact.phone.Phone;
import com.dio.desafio.domain.proposal.contact.phone.enums.PhoneTypeEnum;
import com.dio.desafio.domain.proposal.person.Customer;
import com.dio.desafio.domain.proposal.person.LegalPerson;
import com.dio.desafio.domain.proposal.person.NaturalPerson;
import com.dio.desafio.domain.proposal.person.enums.CivilStatusEnum;
import com.dio.desafio.domain.proposal.person.enums.GenderEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/**
 * Converts between the Portuguese JSON contract of the contracting endpoints and the application/domain model.
 */
@Component
public class ContractWebMapper {

    public ContractProposalCommand toCommand(ContratarPropostaRequest request) {
        List<Violation> violations = new ArrayList<>();
        ClienteDto cliente = request.cliente();
        GenderEnum gender = parse("cliente.sexo", cliente.sexo(), GenderEnum::fromCode, violations);
        CivilStatusEnum civilStatus = parse("cliente.estadoCivil", cliente.estadoCivil(),
                CivilStatusEnum::fromDescription, violations);
        CustomerData customer = new CustomerData(cliente.nome(), cliente.dataNascimento(), gender, civilStatus,
                cliente.razaoSocial(), cliente.nomeFantasia());
        EnderecoDto e = request.endereco();
        Address address = new Address(parse("endereco.tipo", e.tipo(), AddressTypeEnum::fromCode, violations),
                e.logradouro(), e.numero(), e.cep(), e.cidade(), e.estado());
        List<Phone> phones = new ArrayList<>();
        for (int i = 0; i < request.contato().telefones().size(); i++) {
            TelefoneDto t = request.contato().telefones().get(i);
            phones.add(new Phone(parse("contato.telefones[" + i + "].tipo", t.tipo(), PhoneTypeEnum::fromCode,
                    violations), t.ddd(), t.numero()));
        }
        if (!violations.isEmpty()) {
            throw new FieldValidationException(violations);
        }
        return new ContractProposalCommand(request.numeroProposta(), customer, address,
                new Contact(request.contato().email(), phones));
    }

    public ContratacaoResponse toResponse(Contract contract) {
        Customer customer = contract.customer();
        Address a = customer.address();
        EnderecoDto endereco = new EnderecoDto(a.type().getCode(), a.streetName(), a.streetNumber(),
                a.postalCode(), a.city(), a.state());
        ContatoDto contato = new ContatoDto(customer.contact().email(), customer.contact().phones().stream()
                .map(p -> new TelefoneDto(p.type().getCode(), p.areaCode(), p.number())).toList());
        return new ContratacaoResponse(contract.id(), contract.proposalId(), customer.document(),
                customer.personType().name(), toCliente(customer), endereco, contato, contract.contractedAt());
    }

    private ClienteDto toCliente(Customer customer) {
        if (customer instanceof NaturalPerson pf) {
            return new ClienteDto(pf.name(), pf.birthDate(), pf.gender().getCode(), pf.civilStatus().getCode(),
                    null, null);
        }
        LegalPerson pj = (LegalPerson) customer;
        return new ClienteDto(null, null, null, null, pj.businessName(), pj.tradeName());
    }

    private <T> T parse(String field, String value, Function<String, T> parser, List<Violation> violations) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return parser.apply(value);
        } catch (IllegalArgumentException exception) {
            violations.add(new Violation(field, "valor inválido: " + value));
            return null;
        }
    }
}
