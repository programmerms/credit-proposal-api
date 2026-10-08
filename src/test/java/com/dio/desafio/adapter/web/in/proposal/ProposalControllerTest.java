package com.dio.desafio.adapter.web.in.proposal;

import com.dio.desafio.adapter.web.in.proposal.dto.CriarPropostaRequest;
import com.dio.desafio.adapter.web.in.proposal.dto.PropostaResponse;
import com.dio.desafio.adapter.web.in.proposal.mapper.ProposalWebMapper;
import com.dio.desafio.application.port.in.GenerateProposalUseCase;
import com.dio.desafio.application.port.in.GetProposalUseCase;
import com.dio.desafio.domain.proposal.Proposal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static com.dio.desafio.support.TestConstants.INTEGER_ONE;
import static com.dio.desafio.support.domain.ProposalSupport.defaultProposal;
import static com.dio.desafio.support.dto.CriarPropostaRequestSupport.defaultCriarPropostaRequest;
import static com.dio.desafio.support.dto.PropostaResponseSupport.defaultPropostaResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ProposalControllerTest {

    private GenerateProposalUseCase generateUseCase;
    private GetProposalUseCase getUseCase;
    private ProposalWebMapper mapper;
    private ProposalController controller;

    @BeforeEach
    void setUp() {
        generateUseCase = mock(GenerateProposalUseCase.class);
        getUseCase = mock(GetProposalUseCase.class);
        mapper = mock(ProposalWebMapper.class);
        controller = new ProposalController(generateUseCase, getUseCase, mapper);
    }

    @Test
    @DisplayName("Given valid proposal details When the controller creates it Then it returns 201 and the proposal data")
    void givenValidProposalDetailsWhenTheControllerCreatesItThenItReturns201AndTheProposalData() {
        final var request = defaultCriarPropostaRequest().create();
        final var proposal = defaultProposal().create();
        final var expected = defaultPropostaResponse().create();
        setupGivenValidProposalDetailsWhenTheControllerCreatesIt(request, proposal, expected);

        final var result = controller.create(request);

        verifyGivenValidProposalDetailsWhenTheControllerCreatesIt(result, expected, request);
    }

    @Test
    @DisplayName("Given an existing proposal id When the controller gets it Then it returns the proposal data")
    void givenExistingProposalIdWhenTheControllerGetsItThenItReturnsTheProposalData() {
        final var id = UUID.randomUUID();
        final var proposal = defaultProposal().create();
        final var expected = defaultPropostaResponse().create();
        setupGivenExistingProposalIdWhenTheControllerGetsIt(id, proposal, expected);

        final var result = controller.get(id);

        verifyGivenExistingProposalIdWhenTheControllerGetsIt(result, expected, id);
    }

    private void setupGivenValidProposalDetailsWhenTheControllerCreatesIt(
            final CriarPropostaRequest request, final Proposal proposal, final PropostaResponse expected) {
        doReturn(proposal).when(generateUseCase)
                .generate(request.documento(), request.produto(), request.valor(), request.prazoMeses());
        doReturn(expected).when(mapper).toResponse(proposal);
    }

    private void verifyGivenValidProposalDetailsWhenTheControllerCreatesIt(
            final ResponseEntity<PropostaResponse> actual, final PropostaResponse expected,
            final CriarPropostaRequest request) {
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(actual.getBody()).isSameAs(expected);
        verify(generateUseCase, times(INTEGER_ONE))
                .generate(request.documento(), request.produto(), request.valor(), request.prazoMeses());
    }

    private void setupGivenExistingProposalIdWhenTheControllerGetsIt(
            final UUID id, final Proposal proposal, final PropostaResponse expected) {
        doReturn(proposal).when(getUseCase).get(id);
        doReturn(expected).when(mapper).toResponse(proposal);
    }

    private void verifyGivenExistingProposalIdWhenTheControllerGetsIt(
            final PropostaResponse actual, final PropostaResponse expected, final UUID id) {
        assertThat(actual).isSameAs(expected);
        verify(getUseCase, times(INTEGER_ONE)).get(id);
    }
}
