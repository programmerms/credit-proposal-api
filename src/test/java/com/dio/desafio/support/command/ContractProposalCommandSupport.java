package com.dio.desafio.support.command;

import com.dio.desafio.application.port.in.ContractProposalCommand;
import java.util.UUID;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static com.dio.desafio.support.command.CustomerDataSupport.defaultNaturalCustomerData;
import static com.dio.desafio.support.domain.AddressSupport.defaultAddress;
import static com.dio.desafio.support.domain.ContactSupport.defaultContact;
import static org.instancio.Select.field;

public final class ContractProposalCommandSupport {

    private ContractProposalCommandSupport() { }

    public static InstancioApi<ContractProposalCommand> defaultContractProposalCommand() {
        return Instancio.of(ContractProposalCommand.class)
                .supply(field(ContractProposalCommand::customer), () -> defaultNaturalCustomerData().create())
                .supply(field(ContractProposalCommand::address), () -> defaultAddress().create())
                .supply(field(ContractProposalCommand::contact), () -> defaultContact().create());
    }

    public static InstancioApi<ContractProposalCommand> defaultContractProposalCommand(final UUID proposalId) {
        return defaultContractProposalCommand().set(field(ContractProposalCommand::proposalId), proposalId);
    }
}
