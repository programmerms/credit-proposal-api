package com.dio.desafio.adapter.web.in.contract;

import com.dio.desafio.adapter.web.in.contract.dto.ContratacaoResponse;
import com.dio.desafio.adapter.web.in.contract.dto.ContratarPropostaRequest;
import com.dio.desafio.adapter.web.in.contract.mapper.ContractWebMapper;
import com.dio.desafio.application.port.in.ContractProposalCommand;
import com.dio.desafio.application.port.in.ContractProposalUseCase;
import com.dio.desafio.application.port.in.GetContractUseCase;
import com.dio.desafio.domain.contract.Contract;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static com.dio.desafio.support.TestConstants.INTEGER_ONE;
import static com.dio.desafio.support.command.ContractProposalCommandSupport.defaultContractProposalCommand;
import static com.dio.desafio.support.domain.ContractSupport.defaultContract;
import static com.dio.desafio.support.dto.ContratacaoResponseSupport.defaultContratacaoResponse;
import static com.dio.desafio.support.dto.ContratarPropostaRequestSupport.defaultContratarPropostaRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ContractControllerTest {

    private ContractProposalUseCase contractUseCase;
    private GetContractUseCase getUseCase;
    private ContractWebMapper mapper;
    private ContractController controller;

    @BeforeEach
    void setUp() {
        contractUseCase = mock(ContractProposalUseCase.class);
        getUseCase = mock(GetContractUseCase.class);
        mapper = mock(ContractWebMapper.class);
        controller = new ContractController(contractUseCase, getUseCase, mapper);
    }

    @Test
    @DisplayName("Given a valid contracting request When the controller contracts Then it returns 201 and the contract data")
    void givenValidContractingRequestWhenTheControllerContractsThenItReturns201AndTheContractData() {
        final var request = defaultContratarPropostaRequest().create();
        final var command = defaultContractProposalCommand().create();
        final var contract = defaultContract().create();
        final var expected = defaultContratacaoResponse().create();
        setupGivenValidContractingRequestWhenTheControllerContracts(request, command, contract, expected);

        final var result = controller.contract(request);

        verifyGivenValidContractingRequestWhenTheControllerContracts(result, expected, command);
    }

    @Test
    @DisplayName("Given an existing contract number When the controller gets it Then the contract data is returned")
    void givenExistingContractNumberWhenTheControllerGetsItThenTheContractDataIsReturned() {
        final var id = UUID.randomUUID();
        final var contract = defaultContract().create();
        final var expected = defaultContratacaoResponse().create();
        setupGivenExistingContractNumberWhenTheControllerGetsIt(id, contract, expected);

        final var result = controller.get(id);

        verifyGivenExistingContractNumberWhenTheControllerGetsIt(result, expected, id);
    }

    private void setupGivenValidContractingRequestWhenTheControllerContracts(
            final ContratarPropostaRequest request, final ContractProposalCommand command,
            final Contract contract, final ContratacaoResponse expected) {
        doReturn(command).when(mapper).toCommand(request);
        doReturn(contract).when(contractUseCase).contract(command);
        doReturn(expected).when(mapper).toResponse(contract);
    }

    private void verifyGivenValidContractingRequestWhenTheControllerContracts(
            final ResponseEntity<ContratacaoResponse> actual, final ContratacaoResponse expected,
            final ContractProposalCommand command) {
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(actual.getBody()).isSameAs(expected);
        verify(contractUseCase, times(INTEGER_ONE)).contract(command);
    }

    private void setupGivenExistingContractNumberWhenTheControllerGetsIt(
            final UUID id, final Contract contract, final ContratacaoResponse expected) {
        doReturn(contract).when(getUseCase).get(id);
        doReturn(expected).when(mapper).toResponse(contract);
    }

    private void verifyGivenExistingContractNumberWhenTheControllerGetsIt(
            final ContratacaoResponse actual, final ContratacaoResponse expected, final UUID id) {
        assertThat(actual).isSameAs(expected);
        verify(getUseCase, times(INTEGER_ONE)).get(id);
    }
}
