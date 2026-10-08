package com.dio.desafio.application.service;

import com.dio.desafio.application.exception.DuplicateContractException;
import com.dio.desafio.application.exception.FieldValidationException;
import com.dio.desafio.application.exception.ProposalException;
import com.dio.desafio.application.port.in.ContractProposalCommand;
import com.dio.desafio.application.port.out.ContractPersistencePort;
import com.dio.desafio.application.port.out.ProposalPersistencePort;
import com.dio.desafio.application.strategy.CustomerStrategySelector;
import com.dio.desafio.application.strategy.LegalPersonCustomerStrategy;
import com.dio.desafio.application.strategy.NaturalPersonCustomerStrategy;
import com.dio.desafio.domain.ProposalStatus;
import com.dio.desafio.domain.contract.Contract;
import com.dio.desafio.domain.proposal.Proposal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static com.dio.desafio.support.TestConstants.CPF;
import static com.dio.desafio.support.TestConstants.INTEGER_ONE;
import static com.dio.desafio.support.command.ContractProposalCommandSupport.defaultContractProposalCommand;
import static com.dio.desafio.support.command.CustomerDataSupport.defaultLegalCustomerData;
import static com.dio.desafio.support.domain.ContractSupport.defaultContract;
import static com.dio.desafio.support.domain.ProposalSupport.defaultProposal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ContractServiceTest {

    private ProposalPersistencePort proposalPort;
    private ContractPersistencePort contractPort;
    private ContractService service;

    @BeforeEach
    void setUp() {
        proposalPort = mock(ProposalPersistencePort.class);
        contractPort = mock(ContractPersistencePort.class);
        service = new ContractService(proposalPort, contractPort,
                new CustomerStrategySelector(new NaturalPersonCustomerStrategy(), new LegalPersonCustomerStrategy()));
    }

    @Test
    @DisplayName("Given a generated PF proposal When contract is called Then it saves the contract and marks the proposal CONTRATADA")
    void givenGeneratedPfProposalWhenContractIsCalledThenItSavesTheContractAndMarksTheProposalContracted() {
        final var proposal = defaultProposal().create();
        final var command = defaultContractProposalCommand(proposal.id()).create();
        setupGivenGeneratedPfProposalWhenContractIsCalledThenItSavesTheContractAndMarksTheProposalContracted(proposal);

        final var result = service.contract(command);

        verifyGivenGeneratedPfProposalWhenContractIsCalledThenItSavesTheContractAndMarksTheProposalContracted(
                result, proposal);
    }

    @Test
    @DisplayName("Given an unknown proposal When contract is called Then PROPOSTA_NAO_ENCONTRADA is thrown")
    void givenUnknownProposalWhenContractIsCalledThenPropostaNaoEncontradaIsThrown() {
        final var command = defaultContractProposalCommand().create();
        setupGivenUnknownProposalWhenContractIsCalledThenPropostaNaoEncontradaIsThrown(command.proposalId());

        final var thrown = catchThrowable(() -> service.contract(command));

        verifyGivenUnknownProposalWhenContractIsCalledThenPropostaNaoEncontradaIsThrown(thrown);
    }

    @Test
    @DisplayName("Given an already contracted proposal When contract is called Then PROPOSTA_JA_CONTRATADA is thrown")
    void givenAlreadyContractedProposalWhenContractIsCalledThenPropostaJaContratadaIsThrown() {
        final var proposal = defaultProposal().set(field(Proposal::status), ProposalStatus.CONTRATADA).create();
        final var command = defaultContractProposalCommand(proposal.id()).create();
        setupGivenAlreadyContractedProposalWhenContractIsCalledThenPropostaJaContratadaIsThrown(proposal);

        final var thrown = catchThrowable(() -> service.contract(command));

        verifyGivenAlreadyContractedProposalWhenContractIsCalledThenPropostaJaContratadaIsThrown(thrown);
    }

    @Test
    @DisplayName("Given a concurrent contract When the port reports a duplicate Then PROPOSTA_JA_CONTRATADA is thrown and the proposal is not updated")
    void givenConcurrentContractWhenThePortReportsADuplicateThenPropostaJaContratadaIsThrownAndProposalIsNotUpdated() {
        final var proposal = defaultProposal().create();
        final var command = defaultContractProposalCommand(proposal.id()).create();
        setupGivenConcurrentContractWhenThePortReportsADuplicate(proposal);

        final var thrown = catchThrowable(() -> service.contract(command));

        verifyGivenConcurrentContractWhenThePortReportsADuplicate(thrown);
    }

    @Test
    @DisplayName("Given PJ data for a PF proposal When contract is called Then the PF Strategy rejects the missing fields")
    void givenPjDataForAPfProposalWhenContractIsCalledThenThePfStrategyRejectsTheMissingFields() {
        final var proposal = defaultProposal().create();
        final var command = defaultContractProposalCommand()
                .set(field(ContractProposalCommand::proposalId), proposal.id())
                .set(field(ContractProposalCommand::customer), defaultLegalCustomerData().create()).create();
        setupGivenPjDataForAPfProposalWhenContractIsCalledThenThePfStrategyRejectsTheMissingFields(proposal);

        final var thrown = catchThrowable(() -> service.contract(command));

        verifyGivenPjDataForAPfProposalWhenContractIsCalledThenThePfStrategyRejectsTheMissingFields(thrown);
    }

    @Test
    @DisplayName("Given an existing contract id When get is called Then the contract is returned")
    void givenExistingContractIdWhenGetIsCalledThenTheContractIsReturned() {
        final var contract = defaultContract().create();
        setupGivenExistingContractIdWhenGetIsCalledThenTheContractIsReturned(contract);

        final var result = service.get(contract.id());

        verifyGivenExistingContractIdWhenGetIsCalledThenTheContractIsReturned(result, contract);
    }

    @Test
    @DisplayName("Given an unknown contract id When get is called Then CONTRATACAO_NAO_ENCONTRADA is thrown")
    void givenUnknownContractIdWhenGetIsCalledThenContratacaoNaoEncontradaIsThrown() {
        final var id = UUID.randomUUID();
        setupGivenUnknownContractIdWhenGetIsCalledThenContratacaoNaoEncontradaIsThrown(id);

        final var thrown = catchThrowable(() -> service.get(id));

        verifyGivenUnknownContractIdWhenGetIsCalledThenContratacaoNaoEncontradaIsThrown(thrown);
    }

    private void stubProposalFound(final Proposal proposal) {
        doReturn(Optional.of(proposal)).when(proposalPort).findById(proposal.id());
    }

    private void setupGivenGeneratedPfProposalWhenContractIsCalledThenItSavesTheContractAndMarksTheProposalContracted(
            final Proposal proposal) {
        stubProposalFound(proposal);
        doAnswer(invocation -> invocation.getArgument(0)).when(contractPort).save(any(Contract.class));
    }

    private void verifyGivenGeneratedPfProposalWhenContractIsCalledThenItSavesTheContractAndMarksTheProposalContracted(
            final Contract actual, final Proposal proposal) {
        assertThat(actual.proposalId()).isEqualTo(proposal.id());
        assertThat(actual.customer().document()).isEqualTo(CPF);
        verify(contractPort, times(INTEGER_ONE)).save(any(Contract.class));
        final var captor = ArgumentCaptor.forClass(Proposal.class);
        verify(proposalPort, times(INTEGER_ONE)).save(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(ProposalStatus.CONTRATADA);
        assertThat(captor.getValue().id()).isEqualTo(proposal.id());
    }

    private void setupGivenUnknownProposalWhenContractIsCalledThenPropostaNaoEncontradaIsThrown(final UUID id) {
        doReturn(Optional.empty()).when(proposalPort).findById(id);
    }

    private void verifyGivenUnknownProposalWhenContractIsCalledThenPropostaNaoEncontradaIsThrown(
            final Throwable actual) {
        assertBusinessCode(actual, "PROPOSTA_NAO_ENCONTRADA");
        assertNothingPersisted();
    }

    private void setupGivenAlreadyContractedProposalWhenContractIsCalledThenPropostaJaContratadaIsThrown(
            final Proposal proposal) {
        stubProposalFound(proposal);
    }

    private void verifyGivenAlreadyContractedProposalWhenContractIsCalledThenPropostaJaContratadaIsThrown(
            final Throwable actual) {
        assertBusinessCode(actual, "PROPOSTA_JA_CONTRATADA");
        assertNothingPersisted();
    }

    private void verifyGivenConcurrentContractWhenThePortReportsADuplicate(final Throwable actual) {
        assertBusinessCode(actual, "PROPOSTA_JA_CONTRATADA");
        verify(proposalPort, never()).save(any(Proposal.class));
    }

    private void setupGivenPjDataForAPfProposalWhenContractIsCalledThenThePfStrategyRejectsTheMissingFields(
            final Proposal proposal) {
        stubProposalFound(proposal);
    }

    private void verifyGivenPjDataForAPfProposalWhenContractIsCalledThenThePfStrategyRejectsTheMissingFields(
            final Throwable actual) {
        assertThat(actual).isInstanceOf(FieldValidationException.class);
        assertNothingPersisted();
    }

    private void verifyGivenExistingContractIdWhenGetIsCalledThenTheContractIsReturned(
            final Contract actual, final Contract expected) {
        assertThat(actual).isSameAs(expected);
    }

    private void verifyGivenUnknownContractIdWhenGetIsCalledThenContratacaoNaoEncontradaIsThrown(
            final Throwable actual) {
        assertBusinessCode(actual, "CONTRATACAO_NAO_ENCONTRADA");
    }

    private void setupGivenConcurrentContractWhenThePortReportsADuplicate(final Proposal proposal) {
        stubProposalFound(proposal);
        doThrow(new DuplicateContractException(new RuntimeException("unique"))).when(contractPort)
                .save(any(Contract.class));
    }

    private void setupGivenExistingContractIdWhenGetIsCalledThenTheContractIsReturned(final Contract contract) {
        doReturn(Optional.of(contract)).when(contractPort).findById(contract.id());
    }

    private void setupGivenUnknownContractIdWhenGetIsCalledThenContratacaoNaoEncontradaIsThrown(final UUID id) {
        doReturn(Optional.empty()).when(contractPort).findById(id);
    }

    private void assertBusinessCode(final Throwable actual, final String expectedCode) {
        assertThat(actual).isInstanceOfSatisfying(ProposalException.class,
                e -> assertThat(e.code()).isEqualTo(expectedCode));
    }

    private void assertNothingPersisted() {
        verify(contractPort, never()).save(any(Contract.class));
        verify(proposalPort, never()).save(any(Proposal.class));
    }
}
